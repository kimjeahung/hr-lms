package com.hr.backend.employee.service;

import com.hr.backend.domain.course.entity.Lecture;
import com.hr.backend.domain.course.entity.LectureProgress;
import com.hr.backend.domain.course.repository.LectureProgressRepository;
import com.hr.backend.domain.course.repository.LectureRepository;
import com.hr.backend.domain.user.entity.User;
import com.hr.backend.employee.exception.ResourceNotFoundException;
import com.hr.backend.employee.util.CurrentUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EmployeeLectureService {
    private final LectureRepository lectureRepository;
    private final LectureProgressRepository lectureProgressRepository;
    private final CurrentUserProvider currentUserProvider;
    private final EmployeeLearningCompletionService completionService;

    @Transactional
    public void completeLecture(Long lectureId) {
        User user = currentUserProvider.getCurrentUser();
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow(() -> new ResourceNotFoundException("Lecture", "lectureId", lectureId));
        LectureProgress progress = lectureProgressRepository.findByUser_UserIdAndLecture_LectureId(user.getUserId(), lectureId)
                .orElseGet(() -> LectureProgress.builder().user(user).lecture(lecture).build());
        progress.complete();
        lectureProgressRepository.save(progress);
        completionService.recalculateEnrollmentProgress(user, lecture.getCourse());
    }
}
