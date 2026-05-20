package com.hr.backend.employee.service;

import com.hr.backend.domain.course.entity.Course;
import com.hr.backend.domain.course.entity.Lecture;
import com.hr.backend.domain.course.entity.LectureProgress;
import com.hr.backend.domain.course.repository.LectureProgressRepository;
import com.hr.backend.domain.enrollment.entity.Certificate;
import com.hr.backend.domain.enrollment.entity.Enrollment;
import com.hr.backend.domain.enrollment.repository.CertificateRepository;
import com.hr.backend.domain.enrollment.repository.EnrollmentRepository;
import com.hr.backend.domain.quiz.repository.AttemptRepository;
// import com.hr.backend.domain.survey.repository.SurveyRepository;
// import com.hr.backend.domain.survey.repository.SurveyResponseRepository;
import com.hr.backend.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EmployeeLearningCompletionService {
    private final LectureProgressRepository lectureProgressRepository;
    private final AttemptRepository attemptRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final CertificateRepository certificateRepository;
    // private final SurveyRepository surveyRepository;
    // private final SurveyResponseRepository surveyResponseRepository;

    @Transactional
    public void completeLectureIfReady(User user, Lecture lecture) {
        boolean allVideosCompleted = lecture.getVideos().isEmpty();
        // 영상 시청 완료 여부는 EmployeeVideoService에서 직접 완료 처리 후 호출하는 흐름도 허용
        boolean quizPassed = attemptRepository.existsByUser_UserIdAndQuiz_Lecture_LectureIdAndPassedTrue(user.getUserId(), lecture.getLectureId())
                || lecture.getVideos().isEmpty();
        if (quizPassed || lecture.getVideos().isEmpty()) {
            LectureProgress progress = lectureProgressRepository.findByUser_UserIdAndLecture_LectureId(user.getUserId(), lecture.getLectureId())
                    .orElseGet(() -> LectureProgress.builder().user(user).lecture(lecture).build());
            progress.complete();
            lectureProgressRepository.save(progress);
            recalculateEnrollmentProgress(user, lecture.getCourse());
        }
    }

    @Transactional
    public void recalculateEnrollmentProgress(User user, Course course) {
        int total = course.getLectures().size();
        int completed = total == 0 ? 0 : (int) lectureProgressRepository.countByUser_UserIdAndLecture_Course_CourseIdAndCompletedTrue(user.getUserId(), course.getCourseId());
        int progress = total == 0 ? 0 : Math.min(100, (completed * 100) / total);
        enrollmentRepository.findAllByUserId(user.getUserId()).stream()
                .filter(e -> e.getRound().getCourse().getCourseId().equals(course.getCourseId()))
                .findFirst()
                .ifPresent(e -> {
                    e.updateProgress(progress);
                    if (canIssueCertificate(user, e)) {
                        e.updateProgress(100);
                        if (!certificateRepository.existsByUser_UserIdAndRound_RoundId(user.getUserId(), e.getRound().getRoundId())) {
                            certificateRepository.save(Certificate.builder().user(user).round(e.getRound()).fileUrl(null).build());
                        }
                    }
                });
    }

    @Transactional(readOnly = true)
    public boolean canIssueCertificate(User user, Enrollment enrollment) {
        Course course = enrollment.getRound().getCourse();
        int totalLectures = course.getLectures().size();
        long completedLectures = lectureProgressRepository.countByUser_UserIdAndLecture_Course_CourseIdAndCompletedTrue(user.getUserId(), course.getCourseId());
        boolean lecturesDone = totalLectures == 0 || completedLectures >= totalLectures;
        boolean examPassed = attemptRepository.existsByUser_UserIdAndExam_Course_CourseIdAndPassedTrue(user.getUserId(), course.getCourseId());
        boolean surveyCompleted = true;
        // boolean surveySubmitted = surveyRepository.findFirstByCourse_CourseIdAndActiveTrue(course.getCourseId())
        //         .map(s -> surveyResponseRepository.existsBySurvey_SurveyIdAndUser_UserId(s.getSurveyId(), user.getUserId()))
        //         .orElse(true);
        return enrollment.getApprovalStatus() == Enrollment.ApprovalStatus.APPROVED && lecturesDone && examPassed && surveyCompleted;
    }
}
