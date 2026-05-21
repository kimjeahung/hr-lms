package com.hr.backend.employee.service;

import com.hr.backend.domain.course.entity.Course;
import com.hr.backend.domain.course.repository.CourseRepository;
import com.hr.backend.domain.qna.entity.QnaAnswer;
import com.hr.backend.domain.qna.entity.QnaQuestion;
import com.hr.backend.domain.qna.repository.QnaAnswerRepository;
import com.hr.backend.domain.qna.repository.QnaQuestionRepository;
import com.hr.backend.domain.user.entity.User;
import com.hr.backend.domain.user.repository.UserRepository;
import com.hr.backend.employee.dto.request.QnaQuestionRequest;
import com.hr.backend.employee.dto.response.QnaResponse;
import com.hr.backend.employee.exception.ResourceNotFoundException;
import com.hr.backend.employee.util.CurrentUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmployeeQnaService {
    private final QnaQuestionRepository qnaQuestionRepository;
    private final QnaAnswerRepository qnaAnswerRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final CurrentUserProvider currentUserProvider;

    public List<QnaResponse> getMyQuestions() {
        User user = currentUserProvider.getCurrentUser();
        return qnaQuestionRepository.findByUserId(user.getUserId()).stream()
                .sorted(Comparator.comparing(QnaQuestion::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .map(this::toDto)
                .toList();
    }

    public List<QnaResponse> getCourseQuestions(Long courseId) {
        return qnaQuestionRepository.findByCourseId(courseId).stream()
                .sorted(Comparator.comparing(QnaQuestion::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .map(this::toDto)
                .toList();
    }

    public QnaResponse getQuestion(Long questionId) {
        QnaQuestion q = qnaQuestionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("QnaQuestion", "questionId", questionId));
        return toDto(q);
    }

    @Transactional
    public QnaResponse createQuestion(QnaQuestionRequest request) {
        User user = currentUserProvider.getCurrentUser();
        courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course", "courseId", request.getCourseId()));

        QnaQuestion question = QnaQuestion.builder()
                .userId(user.getUserId())
                .courseId(request.getCourseId())
                .title(request.getTitle())
                .content(request.getContent())
                .isResolved(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return toDto(qnaQuestionRepository.save(question));
    }

    private QnaResponse toDto(QnaQuestion q) {
        String courseTitle = courseRepository.findById(q.getCourseId())
                .map(Course::getTitle)
                .orElse(null);

        List<QnaResponse.AnswerItem> answers = qnaAnswerRepository.findByQuestionId(q.getQuestionId()).stream()
                .map(a -> QnaResponse.AnswerItem.builder()
                        .answerId(a.getAnswerId())
                        .authorName(userRepository.findById(a.getAuthorId()).map(User::getName).orElse(null))
                        .content(a.getContent())
                        .createdAt(a.getCreatedAt())
                        .build())
                .toList();

        return QnaResponse.builder()
                .questionId(q.getQuestionId())
                .courseId(q.getCourseId())
                .courseTitle(courseTitle)
                .title(q.getTitle())
                .content(q.getContent())
                .resolved(q.isResolved())
                .createdAt(q.getCreatedAt())
                .answers(answers)
                .build();
    }
}
