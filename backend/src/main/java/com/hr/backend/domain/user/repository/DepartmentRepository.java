package com.hr.backend.domain.user.repository;

import com.hr.backend.domain.user.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department, Integer> {
}
