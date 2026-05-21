package com.hr.backend.domain.qna.service;

import com.hr.backend.admin.dto.QnaAnswerRequest;
import com.hr.backend.admin.dto.QnaAnswerResponse;
import com.hr.backend.admin.dto.QnaQuestionResponse;
import com.hr.backend.domain.qna.entity.QnaAnswer;
import com.hr.backend.domain.qna.entity.QnaQuestion;
import com.hr.backend.domain.qna.repository.QnaAnswerRepository;
import com.hr.backend.domain.qna.repository.QnaQuestionRepository;
import com.hr.backend.domain.user.entity.User;
import com.hr.backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QnaAdminService {

    private final QnaQuestionRepository questionRepo;
    private final QnaAnswerRepository   answerRepo;
    private final UserRepository        userRepository;

    // ── 질문 조회 ────────────────────────────────────────────

    /** 전체 질문 목록 (미답변만 필터 가능) */
    @Transactional(readOnly = true)
    public List<QnaQuestionResponse> getAll(boolean unansweredOnly) {
        List<QnaQuestion> list = unansweredOnly
                ? questionRepo.findAllByResolvedFalseOrderByCreatedAtDesc()
                : questionRepo.findAllByOrderByCreatedAtDesc();
        return list.stream().map(QnaQuestionResponse::new).toList();
    }

    /** 강좌별 질문 목록 */
    @Transactional(readOnly = true)
    public List<QnaQuestionResponse> getByCourse(Long courseId) {
        return questionRepo.findAllByCourse_CourseIdOrderByCreatedAtDesc(courseId)
                .stream().map(QnaQuestionResponse::new).toList();
    }

    /** 질문 단건 조회 (답변 포함) */
    @Transactional(readOnly = true)
    public QnaQuestionResponse getOne(Long questionId) {
        return new QnaQuestionResponse(findQuestion(questionId));
    }

    // ── 답변 관리 ────────────────────────────────────────────

    /** 답변 작성 */
    @Transactional
    public QnaAnswerResponse addAnswer(Long questionId, QnaAnswerRequest req) {
        QnaQuestion question = findQuestion(questionId);
        User admin = getLoginUser();

        QnaAnswer answer = QnaAnswer.builder()
                .question(question)
                .author(admin)
                .content(req.getContent())
                .build();

        return new QnaAnswerResponse(answerRepo.save(answer));
    }

    /** 답변 수정 */
    @Transactional
    public QnaAnswerResponse updateAnswer(Long answerId, QnaAnswerRequest req) {
        QnaAnswer answer = findAnswer(answerId);
        answer.update(req.getContent());
        return new QnaAnswerResponse(answer);
    }

    /** 답변 삭제 */
    @Transactional
    public void deleteAnswer(Long answerId) {
        answerRepo.delete(findAnswer(answerId));
    }

    // ── 해결 처리 ────────────────────────────────────────────

    /** 질문 해결 처리 */
    @Transactional
    public QnaQuestionResponse resolve(Long questionId) {
        QnaQuestion question = findQuestion(questionId);
        question.resolve();
        return new QnaQuestionResponse(question);
    }

    // ── private helpers ──────────────────────────────────────

    private QnaQuestion findQuestion(Long id) {
        return questionRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("질문을 찾을 수 없습니다. id=" + id));
    }

    private QnaAnswer findAnswer(Long id) {
        return answerRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("답변을 찾을 수 없습니다. id=" + id));
    }

    private User getLoginUser() {
        String employeeNo = (String) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return userRepository.findByEmployeeNo(employeeNo)
                .orElseThrow(() -> new IllegalStateException("인증된 사용자를 찾을 수 없습니다."));
    }
}
