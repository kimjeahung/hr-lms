package com.hr.backend.domain.quiz.service;

import com.hr.backend.admin.dto.*;
import com.hr.backend.domain.course.entity.Lecture;
import com.hr.backend.domain.course.repository.LectureRepository;
import com.hr.backend.domain.quiz.entity.Choice;
import com.hr.backend.domain.quiz.entity.Question;
import com.hr.backend.domain.quiz.entity.Quiz;
import com.hr.backend.domain.quiz.repository.QuestionRepository;
import com.hr.backend.domain.quiz.repository.QuizRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuizService {

    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final LectureRepository lectureRepository;

    /** 강의별 퀴즈 조회 (문항+선택지 포함) */
    public QuizResponse getByLecture(Long lectureId) {
        Quiz quiz = quizRepository.findByLecture_LectureId(lectureId)
                .orElseThrow(() -> new IllegalArgumentException("해당 강의의 퀴즈가 없습니다."));
        return new QuizResponse(quiz);
    }

    /** 퀴즈 생성 */
    @Transactional
    public QuizResponse create(Long lectureId, QuizRequest req) {
        if (quizRepository.existsByLecture_LectureId(lectureId)) {
            throw new IllegalArgumentException("해당 강의에 이미 퀴즈가 존재합니다.");
        }
        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new IllegalArgumentException("강의를 찾을 수 없습니다."));

        Quiz quiz = Quiz.builder()
                .lecture(lecture)
                .title(req.getTitle())
                .passScore(req.getPassScore())
                .build();
        return new QuizResponse(quizRepository.save(quiz));
    }

    /** 퀴즈 수정 (제목, 합격점수) */
    @Transactional
    public QuizResponse update(Long lectureId, QuizRequest req) {
        Quiz quiz = quizRepository.findByLecture_LectureId(lectureId)
                .orElseThrow(() -> new IllegalArgumentException("해당 강의의 퀴즈가 없습니다."));
        quiz.update(req.getTitle(), req.getPassScore());
        return new QuizResponse(quiz);
    }

    /** 퀴즈 삭제 */
    @Transactional
    public void delete(Long lectureId) {
        Quiz quiz = quizRepository.findByLecture_LectureId(lectureId)
                .orElseThrow(() -> new IllegalArgumentException("해당 강의의 퀴즈가 없습니다."));
        quizRepository.delete(quiz);
    }

    /** 문항 추가 (선택지 포함) */
    @Transactional
    public QuizResponse addQuestion(Long lectureId, QuestionRequest req) {
        Quiz quiz = quizRepository.findByLecture_LectureId(lectureId)
                .orElseThrow(() -> new IllegalArgumentException("해당 강의의 퀴즈가 없습니다."));

        Question question = Question.builder()
                .quiz(quiz)
                .exam(null)
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
        quiz.getQuestions().add(question);
        return new QuizResponse(quiz);
    }

    /** 문항 수정 */
    @Transactional
    public QuizResponse updateQuestion(Long lectureId, Long questionId, QuestionRequest req) {
        Quiz quiz = quizRepository.findByLecture_LectureId(lectureId)
                .orElseThrow(() -> new IllegalArgumentException("해당 강의의 퀴즈가 없습니다."));

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("문항을 찾을 수 없습니다."));

        question.update(req.getQuestionText(), req.getScore(), req.getSortOrder());

        // 선택지 전체 교체
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
        return new QuizResponse(quiz);
    }

    /** 문항 삭제 */
    @Transactional
    public QuizResponse deleteQuestion(Long lectureId, Long questionId) {
        Quiz quiz = quizRepository.findByLecture_LectureId(lectureId)
                .orElseThrow(() -> new IllegalArgumentException("해당 강의의 퀴즈가 없습니다."));
        quiz.getQuestions().removeIf(q -> q.getQuestionId().equals(questionId));
        return new QuizResponse(quiz);
    }
}
