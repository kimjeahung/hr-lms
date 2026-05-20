package com.hr.backend.employee.service;

import com.hr.backend.domain.enrollment.entity.Certificate;
import com.hr.backend.domain.enrollment.entity.Enrollment;
import com.hr.backend.domain.enrollment.repository.CertificateRepository;
import com.hr.backend.domain.enrollment.repository.EnrollmentRepository;
import com.hr.backend.domain.user.entity.User;
import com.hr.backend.domain.user.repository.UserRepository;
import com.hr.backend.employee.dto.response.MypageResponse;
import com.hr.backend.employee.exception.ResourceNotFoundException;
import com.hr.backend.employee.util.CurrentUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmployeeMypageService {
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final CertificateRepository certificateRepository;
    private final CurrentUserProvider currentUserProvider;

    public MypageResponse getMyPageInfo() {
        User user = getCurrentUser();
        List<Enrollment> enrollments = enrollmentRepository.findAllByUserId(user.getUserId());
        double avg = enrollments.stream().mapToInt(Enrollment::getProgress).average().orElse(0.0);
        long completed = enrollments.stream().filter(e -> e.getStatus() == Enrollment.Status.DONE).count();
        List<MypageResponse.MypageCertificateItem> certs = certificateRepository.findAll().stream()
                .filter(c -> c.getUser().getUserId().equals(user.getUserId()))
                .map(c -> MypageResponse.MypageCertificateItem.builder().certificateId(c.getCertificateId())
                        .courseTitle(c.getRound().getCourse().getTitle()).fileURL(c.getFileUrl()).build()).toList();
        return MypageResponse.builder().userId(user.getUserId()).employeeNo(user.getEmployeeNo()).name(user.getName())
                .email(user.getEmail()).departmentName(user.getDepartment()!=null?user.getDepartment().getName():null)
                .position(user.getPosition()).empType(String.valueOf(user.getEmpType())).hireDate(user.getHireDate())
                .overallCompletionRate(avg).completedCoursesCount(completed).certificates(certs).build();
    }
    private User getCurrentUser(){Long id=currentUserProvider.getCurrentUserId();return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", "userId", id));}
}
