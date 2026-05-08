package com.hr.backend.domain.dashboard;

import com.hr.backend.admin.dto.DashboardResponse;
import com.hr.backend.domain.course.entity.CourseRound;
import com.hr.backend.domain.course.repository.CourseRoundRepository;
import com.hr.backend.domain.course.repository.CourseRepository;
import com.hr.backend.domain.enrollment.entity.Enrollment;
import com.hr.backend.domain.enrollment.repository.EnrollmentRepository;
import com.hr.backend.domain.user.entity.User;
import com.hr.backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private final UserRepository       userRepository;
    private final CourseRepository     courseRepository;
    private final CourseRoundRepository courseRoundRepository;
    private final EnrollmentRepository enrollmentRepository;

    public DashboardResponse getDashboard() {

        List<User>       users       = userRepository.findAll();
        List<Enrollment> enrollments = enrollmentRepository.findAllWithUserAndCourse();

        int totalEmployees = users.size();
        int totalCourses   = courseRepository.findAllByActiveTrue().size();

        // 전체 이수율: 전체 enrollment 중 DONE 비율
        long doneCount = enrollments.stream()
                .filter(e -> e.getStatus() == Enrollment.Status.DONE).count();
        double overallRate = enrollments.isEmpty() ? 0
                : Math.round(doneCount * 1000.0 / enrollments.size()) / 10.0;

        // 미이수자 수: 수강 중인데 DONE이 아닌 직원
        long notCompleted = users.stream()
                .filter(u -> !"ROLE_ADMIN".equals(u.getRole()))
                .filter(u -> {
                    List<Enrollment> userEnrollments = enrollments.stream()
                            .filter(e -> e.getUser().getUserId().equals(u.getUserId()))
                            .toList();
                    if (userEnrollments.isEmpty()) return true;
                    return userEnrollments.stream().anyMatch(e -> e.getStatus() != Enrollment.Status.DONE);
                }).count();

        // 부서별 이수율
        Map<String, List<Enrollment>> byDept = enrollments.stream()
                .filter(e -> e.getUser().getDepartment() != null)
                .collect(Collectors.groupingBy(e -> e.getUser().getDepartment().getName()));

        List<DashboardResponse.DeptStat> deptStats = byDept.entrySet().stream()
                .map(entry -> {
                    long done  = entry.getValue().stream().filter(e -> e.getStatus() == Enrollment.Status.DONE).count();
                    double rate = Math.round(done * 1000.0 / entry.getValue().size()) / 10.0;
                    return DashboardResponse.DeptStat.builder()
                            .dept(entry.getKey()).completionRate(rate).build();
                })
                .sorted(Comparator.comparingDouble(DashboardResponse.DeptStat::getCompletionRate).reversed())
                .toList();

        // 미이수자 상위 (진행률 낮은 순)
        Map<Long, List<Enrollment>> byUser = enrollments.stream()
                .collect(Collectors.groupingBy(e -> e.getUser().getUserId()));

        List<DashboardResponse.LowProgress> lowList = byUser.entrySet().stream()
                .map(entry -> {
                    User u = entry.getValue().get(0).getUser();
                    double rate = entry.getValue().stream()
                            .mapToInt(Enrollment::getProgress).average().orElse(0);
                    String deptName = u.getDepartment() != null ? u.getDepartment().getName() : "";
                    return DashboardResponse.LowProgress.builder()
                            .userId(u.getUserId()).name(u.getName())
                            .department(deptName)
                            .role(u.getRole())
                            .completionRate(Math.round(rate * 10.0) / 10.0)
                            .build();
                })
                .filter(lp -> lp.getCompletionRate() < 100)
                .sorted(Comparator.comparingDouble(DashboardResponse.LowProgress::getCompletionRate))
                .limit(5)
                .toList();

        // 마감 임박 알림 (course_rounds 기준 — endDate가 오늘 이후인 차수)
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        List<DashboardResponse.DeadlineAlert> alerts = courseRoundRepository.findAll().stream()
                .filter(r -> !r.getEndDate().isBefore(LocalDate.now()))
                .sorted(Comparator.comparing(CourseRound::getEndDate))
                .limit(5)
                .map(r -> {
                    long notDone = enrollments.stream()
                            .filter(e -> e.getRound().getRoundId().equals(r.getRoundId()))
                            .filter(e -> e.getStatus() != Enrollment.Status.DONE)
                            .count();
                    return DashboardResponse.DeadlineAlert.builder()
                            .courseId(r.getCourse().getCourseId())
                            .title(r.getCourse().getTitle() + " (" + r.getRoundNo() + "차)")
                            .deadline(r.getEndDate().format(fmt))
                            .notCompletedCount((int) notDone)
                            .build();
                })
                .toList();

        return DashboardResponse.builder()
                .totalEmployees(totalEmployees)
                .totalCourses(totalCourses)
                .notCompletedCount((int) notCompleted)
                .overallCompletionRate(overallRate)
                .deptStats(deptStats)
                .lowProgressList(lowList)
                .deadlineAlerts(alerts)
                .build();
    }
}
