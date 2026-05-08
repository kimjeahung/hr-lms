package com.hr.backend.admin.controller;

import com.hr.backend.domain.user.entity.Department;
import com.hr.backend.domain.user.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentRepository departmentRepository;

    /** 부서 목록 조회 */
    @GetMapping
    public ResponseEntity<List<Department>> getAll() {
        return ResponseEntity.ok(departmentRepository.findAll());
    }

    /** 부서 등록 */
    @PostMapping
    public ResponseEntity<Department> create(@RequestBody Map<String, String> body) {
        Department dept = Department.builder()
                .name(body.get("name"))
                .build();
        return ResponseEntity.ok(departmentRepository.save(dept));
    }
}
