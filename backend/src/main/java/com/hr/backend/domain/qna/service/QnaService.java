package com.hr.backend.domain.qna.service;

import com.hr.backend.domain.qna.dto.QnaQuestionRequest;
import com.hr.backend.domain.qna.dto.QnaAnswerRequest;
import com.hr.backend.domain.qna.entity.QnaQuestion;
import com.hr.backend.domain.qna.entity.QnaAnswer;
import com.hr.backend.domain.qna.repository.QnaQuestionRepository;
import com.hr.backend.domain.qna.repository.QnaAnswerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QnaService {
    private final QnaQuestionRepository questionRepo;
    private final QnaAnswerRepository answerRepo;

    public QnaQuestion createQuestion(Long userId, QnaQuestionRequest req) {
        QnaQuestion q = QnaQuestion.builder()
                .courseId(req.getCourseId())
                .userId(userId)
                .title(req.getTitle())
                .content(req.getContent())
                .isResolved(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        return questionRepo.save(q);
    }

    public List<QnaQuestion> getQuestionsByCourse(Long courseId) {
        return questionRepo.findByCourseId(courseId);
    }
    public List<QnaQuestion> getQuestionsByUser(Long userId) {
        return questionRepo.findByUserId(userId);
    }
    public QnaQuestion getQuestion(Long questionId) {
        return questionRepo.findById(questionId).orElseThrow();
    }

    public QnaAnswer createAnswer(Long authorId, QnaAnswerRequest req) {
        QnaAnswer a = QnaAnswer.builder()
                .questionId(req.getQuestionId())
                .authorId(authorId)
                .content(req.getContent())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        return answerRepo.save(a);
    }
    public List<QnaAnswer> getAnswers(Long questionId) {
        return answerRepo.findByQuestionId(questionId);
    }

    // 질문 수정
    public QnaQuestion updateQuestion(Long questionId, QnaQuestionRequest req) {
        QnaQuestion q = questionRepo.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("질문을 찾을 수 없습니다."));
        q = QnaQuestion.builder()
                .questionId(q.getQuestionId())
                .courseId(q.getCourseId())
                .userId(q.getUserId())
                .title(req.getTitle())
                .content(req.getContent())
                .isResolved(q.isResolved())
                .createdAt(q.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();
        return questionRepo.save(q);
    }

    // 질문 삭제
    public void deleteQuestion(Long questionId) {
        questionRepo.deleteById(questionId);
    }

    // 답변 수정
    public QnaAnswer updateAnswer(Long answerId, QnaAnswerRequest req) {
        QnaAnswer a = answerRepo.findById(answerId)
                .orElseThrow(() -> new IllegalArgumentException("답변을 찾을 수 없습니다."));
        a = QnaAnswer.builder()
                .answerId(a.getAnswerId())
                .questionId(a.getQuestionId())
                .authorId(a.getAuthorId())
                .content(req.getContent())
                .createdAt(a.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();
        return answerRepo.save(a);
    }

    // 답변 삭제
    public void deleteAnswer(Long answerId) {
        answerRepo.deleteById(answerId);
    }
}