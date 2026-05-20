package com.hr.backend.domain.enrollment.controller;

import com.hr.backend.domain.course.entity.Course;
import com.hr.backend.domain.course.entity.CourseRound;
import com.hr.backend.domain.course.repository.CourseRepository;
import com.hr.backend.domain.course.repository.CourseRoundRepository;
import com.hr.backend.domain.enrollment.entity.Enrollment;
import com.hr.backend.domain.enrollment.repository.EnrollmentRepository;
import com.hr.backend.domain.user.entity.Department;
import com.hr.backend.domain.user.entity.User;
import com.hr.backend.domain.user.repository.DepartmentRepository;
import com.hr.backend.domain.user.repository.UserRepository;
import com.hr.backend.security.jwt.JwtProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class EnrollmentUserAuthorizationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CourseRoundRepository courseRoundRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Test
    void 본인은_내_수강상세를_조회할_수_있다() throws Exception {
        Fixture fixture = createFixture();

        mockMvc.perform(get("/api/user/enrollments/{enrollmentId}", fixture.enrollment.getEnrollmentId())
                        .header("Authorization", bearerToken(fixture.owner)))
                .andExpect(status().isOk());
    }

    @Test
    void 타인은_다른_사용자의_수강상세를_조회할_수_없다() throws Exception {
        Fixture fixture = createFixture();

        mockMvc.perform(get("/api/user/enrollments/{enrollmentId}", fixture.enrollment.getEnrollmentId())
                        .header("Authorization", bearerToken(fixture.otherUser)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void 관리자는_다른_사용자의_수강상세를_조회할_수_있다() throws Exception {
        Fixture fixture = createFixture();

        mockMvc.perform(get("/api/user/enrollments/{enrollmentId}", fixture.enrollment.getEnrollmentId())
                        .header("Authorization", bearerToken(fixture.admin)))
                .andExpect(status().isOk());
    }

    @Test
    void 본인은_내_진행률을_수정할_수_있다() throws Exception {
        Fixture fixture = createFixture();

        mockMvc.perform(put("/api/user/enrollments/{enrollmentId}/progress", fixture.enrollment.getEnrollmentId())
                        .queryParam("progress", "30")
                        .header("Authorization", bearerToken(fixture.owner)))
                .andExpect(status().isOk());

        Enrollment updated = enrollmentRepository.findById(fixture.enrollment.getEnrollmentId()).orElseThrow();
        assertThat(updated.getProgress()).isEqualTo(30);
    }

    @Test
    void 타인은_다른_사용자의_진행률을_수정할_수_없다() throws Exception {
        Fixture fixture = createFixture();

        mockMvc.perform(put("/api/user/enrollments/{enrollmentId}/progress", fixture.enrollment.getEnrollmentId())
                        .queryParam("progress", "55")
                        .header("Authorization", bearerToken(fixture.otherUser)))
                .andExpect(status().isBadRequest());

        Enrollment unchanged = enrollmentRepository.findById(fixture.enrollment.getEnrollmentId()).orElseThrow();
        assertThat(unchanged.getProgress()).isEqualTo(0);
    }

    @Test
    void 관리자는_다른_사용자의_진행률을_수정할_수_있다() throws Exception {
        Fixture fixture = createFixture();

        mockMvc.perform(put("/api/user/enrollments/{enrollmentId}/progress", fixture.enrollment.getEnrollmentId())
                        .queryParam("progress", "60")
                        .header("Authorization", bearerToken(fixture.admin)))
                .andExpect(status().isOk());

        Enrollment updated = enrollmentRepository.findById(fixture.enrollment.getEnrollmentId()).orElseThrow();
        assertThat(updated.getProgress()).isEqualTo(60);
    }

    private Fixture createFixture() {
        Department department = departmentRepository.save(Department.builder()
                .name("테스트부서")
                .build());

        User owner = userRepository.save(User.builder()
                .employeeNo("U10001")
                .name("수강자")
                .email("u10001@test.com")
                .rawPassword("Password1!")
                .department(department)
                .position("사원")
                .empType(0)
                .role("ROLE_USER")
                .phone("010-1000-0001")
                .hireDate(LocalDate.now())
                .encoder(passwordEncoder)
                .build());

        User otherUser = userRepository.save(User.builder()
                .employeeNo("U10002")
                .name("타사용자")
                .email("u10002@test.com")
                .rawPassword("Password1!")
                .department(department)
                .position("사원")
                .empType(0)
                .role("ROLE_USER")
                .phone("010-1000-0002")
                .hireDate(LocalDate.now())
                .encoder(passwordEncoder)
                .build());

        User admin = userRepository.save(User.builder()
                .employeeNo("A10001")
                .name("관리자")
                .email("a10001@test.com")
                .rawPassword("Password1!")
                .department(department)
                .position("관리자")
                .empType(0)
                .role("ROLE_ADMIN")
                .phone("010-1000-0003")
                .hireDate(LocalDate.now())
                .encoder(passwordEncoder)
                .build());

        Course course = courseRepository.save(Course.builder()
                .title("권한검증용 강좌")
                .description("desc")
                .category("직무교육")
                .targetRole(0)
                .durationMin(120)
                .thumbnailUrl("thumb")
                .build());

        CourseRound round = courseRoundRepository.save(CourseRound.builder()
                .course(course)
                .roundNo(1)
                .startDate(LocalDate.now().minusDays(1))
                .endDate(LocalDate.now().plusDays(30))
                .build());

        Enrollment enrollment = enrollmentRepository.save(Enrollment.builder()
                .user(owner)
                .round(round)
                .build());

        return new Fixture(owner, otherUser, admin, enrollment);
    }

    private String bearerToken(User user) {
        return "Bearer " + jwtProvider.generate(user.getEmployeeNo(), user.getRole());
    }

    private record Fixture(User owner, User otherUser, User admin, Enrollment enrollment) {}
}
