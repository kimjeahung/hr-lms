package com.hr.backend.admin.controller;

import com.hr.backend.admin.dto.EmployeeRequest;
import com.hr.backend.admin.dto.EmployeeResponse;
import com.hr.backend.domain.user.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/admin/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping
    public ResponseEntity<List<EmployeeResponse>> getAll(
            @RequestParam(required = false) String keyword) {
        return ResponseEntity.ok(employeeService.getAll(keyword));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponse> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.getOne(id));
    }

    @PostMapping
    public ResponseEntity<EmployeeResponse> register(@Valid @RequestBody EmployeeRequest req) {
        return ResponseEntity.ok(employeeService.register(req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmployeeResponse> update(
            @PathVariable Long id, @Valid @RequestBody EmployeeRequest req) {
        return ResponseEntity.ok(employeeService.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        employeeService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/excel")
    public ResponseEntity<List<EmployeeResponse>> registerByExcel(
            @RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(employeeService.registerByExcel(file));
    }
}
