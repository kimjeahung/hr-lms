package com.hr.backend.domain.qna.service;

import com.hr.backend.domain.course.entity.Course;
import com.hr.backend.domain.course.repository.CourseRepository;
import com.hr.backend.domain.qna.dto.QnaAnswerRequest;
import com.hr.backend.domain.qna.dto.QnaQuestionRequest;
import com.hr.backend.domain.qna.entity.QnaAnswer;
import com.hr.backend.domain.qna.entity.QnaQuestion;
import com.hr.backend.domain.qna.repository.QnaAnswerRepository;
import com.hr.backend.domain.qna.repository.QnaQuestionRepository;
import com.hr.backend.domain.user.entity.User;
import com.hr.backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QnaService {

    private final QnaQuestionRepository questionRepo;
    private final QnaAnswerRepository   answerRepo;
    private final CourseRepository      courseRepository;
    private final UserRepository        userRepository;

    @Transactional
    public QnaQuestion createQuestion(Long userId, QnaQuestionRequest req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Course course = courseRepository.findById(req.getCourseId())
                .orElseThrow(() -> new IllegalArgumentException("과정을 찾을 수 없습니다."));

        QnaQuestion q = QnaQuestion.builder()
                .course(course)
                .user(user)
                .title(req.getTitle())
                .content(req.getContent())
                .build();
        return questionRepo.save(q);
    }

    public List<QnaQuestion> getQuestionsByCourse(Long courseId) {
        return questionRepo.findAllByCourse_CourseIdOrderByCreatedAtDesc(courseId);
    }

    public List<QnaQuestion> getQuestionsByUser(Long userId) {
        return questionRepo.findAllByUser_UserIdOrderByCreatedAtDesc(userId);
    }

    public QnaQuestion getQuestion(Long questionId) {
        return questionRepo.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("질문을 찾을 수 없습니다."));
    }

    @Transactional
    public QnaAnswer createAnswer(Long authorId, QnaAnswerRequest req) {
        QnaQuestion question = questionRepo.findById(req.getQuestionId())
                .orElseThrow(() -> new IllegalArgumentException("질문을 찾을 수 없습니다."));
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        QnaAnswer a = QnaAnswer.builder()
                .question(question)
                .author(author)
                .content(req.getContent())
                .build();
        return answerRepo.save(a);
    }

    public List<QnaAnswer> getAnswers(Long questionId) {
        return answerRepo.findByQuestion_QuestionId(questionId);
    }

    @Transactional
    public QnaQuestion updateQuestion(Long questionId, QnaQuestionRequest req) {
        QnaQuestion q = questionRepo.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("질문을 찾을 수 없습니다."));
        q.updateContent(req.getTitle(), req.getContent());
        return q;
    }

    @Transactional
    public void deleteQuestion(Long questionId) {
        questionRepo.deleteById(questionId);
    }

    @Transactional
    public QnaAnswer updateAnswer(Long answerId, QnaAnswerRequest req) {
        QnaAnswer a = answerRepo.findById(answerId)
                .orElseThrow(() -> new IllegalArgumentException("답변을 찾을 수 없습니다."));
        a.update(req.getContent());
        return a;
    }

    @Transactional
    public void deleteAnswer(Long answerId) {
        answerRepo.deleteById(answerId);
    }
}
