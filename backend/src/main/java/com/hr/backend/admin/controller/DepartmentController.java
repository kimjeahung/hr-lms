package com.hr.backend.admin.controller;

import com.hr.backend.domain.user.entity.Department;
import com.hr.backend.domain.user.repository.DepartmentRepository;
import com.hr.backend.domain.user.repository.UserRepository;
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
    private final UserRepository       userRepository;

    /** 부서 목록 조회 */
    @GetMapping
    public ResponseEntity<List<Department>> getAll() {
        return ResponseEntity.ok(departmentRepository.findAll());
    }

    /** 부서 단건 조회 */
    @GetMapping("/{id}")
    public ResponseEntity<Department> getOne(@PathVariable Integer id) {
        return ResponseEntity.ok(findById(id));
    }

    /** 부서 등록 */
    @PostMapping
    public ResponseEntity<Department> create(@RequestBody Map<String, String> body) {
        String name = body.get("name");
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("부서명은 필수입니다.");
        }
        Department dept = Department.builder().name(name.trim()).build();
        return ResponseEntity.ok(departmentRepository.save(dept));
    }

    /** 부서명 수정 */
    @PutMapping("/{id}")
    public ResponseEntity<Department> update(
            @PathVariable Integer id,
            @RequestBody Map<String, String> body) {
        String name = body.get("name");
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("부서명은 필수입니다.");
        }
        Department dept = findById(id);
        dept.updateName(name.trim());
        return ResponseEntity.ok(departmentRepository.save(dept));
    }

    /**
     * 부서 삭제.
     * 소속 직원이 있으면 삭제 불가 (409 Conflict).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        findById(id);   // 존재 확인
        long memberCount = userRepository.countByDepartment_DepartmentId(id);
        if (memberCount > 0) {
            throw new IllegalStateException(
                    "소속 직원이 " + memberCount + "명 있어 삭제할 수 없습니다. 직원 이동 후 삭제하세요.");
        }
        departmentRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private Department findById(Integer id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("부서를 찾을 수 없습니다. id=" + id));
    }
}
