package com.hr.backend.domain.user.repository;

import com.hr.backend.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmployeeNo(String employeeNo);
    boolean existsByEmployeeNo(String employeeNo);
    boolean existsByEmail(String email);
}
