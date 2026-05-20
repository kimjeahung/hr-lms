package com.hr.backend;

import com.hr.backend.domain.user.entity.Department;
import com.hr.backend.domain.user.entity.User;
import com.hr.backend.domain.user.repository.DepartmentRepository;
import com.hr.backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * 앱 최초 실행 시 테스트용 데이터 자동 생성
 * - 관리자 계정: ADMIN001 / ADMIN001 (사번 = 초기 비밀번호)
 * - 기본 부서: 관리팀
 */
@Slf4j
@Component
@Profile("local")
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final UserRepository       userRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder      passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        // 부서가 없으면 기본 부서 생성
        if (departmentRepository.count() == 0) {
            departmentRepository.save(Department.builder().name("관리팀").build());
            departmentRepository.save(Department.builder().name("개발팀").build());
            departmentRepository.save(Department.builder().name("현장팀").build());
            log.info("[DataInitializer] 기본 부서 3개 생성 완료");
        }

        // 관리자 계정이 없으면 생성
        if (!userRepository.existsByEmployeeNo("ADMIN001")) {
            Department dept = departmentRepository.findAll().get(0);
            User admin = User.builder()
                    .employeeNo("ADMIN001")
                    .name("시스템관리자")
                    .email("admin@company.com")
                    .rawPassword("ADMIN001")   // 초기 비밀번호 = 사번
                    .department(dept)
                    .position("관리자")
                    .empType(0)
                    .role("ROLE_ADMIN")
                    .phone("010-0000-0000")
                    .hireDate(LocalDate.now())
                    .encoder(passwordEncoder)
                    .build();
            userRepository.save(admin);
            log.info("[DataInitializer] 관리자 계정 생성 완료 (ADMIN001 / ADMIN001)");
        }
    }
}
