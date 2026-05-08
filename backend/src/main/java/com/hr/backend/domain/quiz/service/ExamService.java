package com.hr.backend.domain.quiz.service;

import com.hr.backend.admin.dto.*;
import com.hr.backend.domain.course.entity.Course;
import com.hr.backend.domain.course.repository.CourseRepository;
import com.hr.backend.domain.quiz.entity.Choice;
import com.hr.backend.domain.quiz.entity.Exam;
import com.hr.backend.domain.quiz.entity.Question;
import com.hr.backend.domain.quiz.repository.ExamRepository;
import com.hr.backend.domain.quiz.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExamService {

    private final ExamRepository examRepository;
    private final QuestionRepository questionRepository;
    private final CourseRepository courseRepository;

    /** 강좌별 시험 조회 (문항+선택지 포함) */
    public ExamResponse getByCourse(Long courseId) {
        Exam exam = examRepository.findByCourse_CourseId(courseId)
                .orElseThrow(() -> new IllegalArgumentException("해당 강좌의 시험이 없습니다."));
        return new ExamResponse(exam);
    }

    /** 시험 생성 */
    @Transactional
    public ExamResponse create(Long courseId, ExamRequest req) {
        if (examRepository.existsByCourse_CourseId(courseId)) {
            throw new IllegalArgumentException("해당 강좌에 이미 시험이 존재합니다.");
        }
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("강좌를 찾을 수 없습니다."));

        Exam exam = Exam.builder()
                .course(course)
                .title(req.getTitle())
                .passScore(req.getPassScore())
                .build();
        return new ExamResponse(examRepository.save(exam));
    }

    /** 시험 수정 (제목, 합격점수) */
    @Transactional
    public ExamResponse update(Long courseId, ExamRequest req) {
        Exam exam = examRepository.findByCourse_CourseId(courseId)
                .orElseThrow(() -> new IllegalArgumentException("해당 강좌의 시험이 없습니다."));
        exam.update(req.getTitle(), req.getPassScore());
        return new ExamResponse(exam);
    }

    /** 시험 삭제 */
    @Transactional
    public void delete(Long courseId) {
        Exam exam = examRepository.findByCourse_CourseId(courseId)
                .orElseThrow(() -> new IllegalArgumentException("해당 강좌의 시험이 없습니다."));
        examRepository.delete(exam);
    }

    /** 문항 추가 (선택지 포함) */
    @Transactional
    public ExamResponse addQuestion(Long courseId, QuestionRequest req) {
        Exam exam = examRepository.findByCourse_CourseId(courseId)
                .orElseThrow(() -> new IllegalArgumentException("해당 강좌의 시험이 없습니다."));

        Question question = Question.builder()
                .quiz(null)
                .exam(exam)
                .questionText(req.getQuestionText())
                .score(req.getScore())
                .sortOrder(req.getSortOrder())
                .build();

        if (req.getChoices() != null) {
            req.getChoices().forEach(c -> question.getChoices().add(
                    Choice.builder()
                            .question(question)
                            .choiceText(c.getChoiceText())
                            .correct(c.isCorrect())
                            .sortOrder(c.getSortOrder())
                            .build()
            ));
        }
        exam.getQuestions().add(question);
        return new ExamResponse(exam);
    }

    /** 문항 수정 */
    @Transactional
    public ExamResponse updateQuestion(Long courseId, Long questionId, QuestionRequest req) {
        Exam exam = examRepository.findByCourse_CourseId(courseId)
                .orElseThrow(() -> new IllegalArgumentException("해당 강좌의 시험이 없습니다."));

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("문항을 찾을 수 없습니다."));

        question.update(req.getQuestionText(), req.getScore(), req.getSortOrder());

        if (req.getChoices() != null) {
            question.getChoices().clear();
            req.getChoices().forEach(c -> question.getChoices().add(
                    Choice.builder()
                            .question(question)
                            .choiceText(c.getChoiceText())
                            .correct(c.isCorrect())
                            .sortOrder(c.getSortOrder())
                            .build()
            ));
        }
        return new ExamResponse(exam);
    }

    /** 문항 삭제 */
    @Transactional
    public ExamResponse deleteQuestion(Long courseId, Long questionId) {
        Exam exam = examRepository.findByCourse_CourseId(courseId)
                .orElseThrow(() -> new IllegalArgumentException("해당 강좌의 시험이 없습니다."));
        exam.getQuestions().removeIf(q -> q.getQuestionId().equals(questionId));
        return new ExamResponse(exam);
    }
}
