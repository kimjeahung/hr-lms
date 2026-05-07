package com.hr.backend.domain.user.service;

import com.hr.backend.admin.dto.EmployeeRequest;
import com.hr.backend.admin.dto.EmployeeResponse;
import com.hr.backend.domain.user.entity.User;
import com.hr.backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmployeeService {

    private final UserRepository  userRepository;
    private final PasswordEncoder passwordEncoder;

    public List<EmployeeResponse> getAll(String keyword) {
        return userRepository.findAll().stream()
                .filter(u -> keyword == null || keyword.isBlank()
                        || u.getName().contains(keyword)
                        || u.getEmployeeNo().contains(keyword)
                        || (u.getDepartment() != null && u.getDepartment().contains(keyword)))
                .map(EmployeeResponse::new)
                .toList();
    }

    public EmployeeResponse getOne(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("직원을 찾을 수 없습니다."));
        return new EmployeeResponse(user);
    }

    @Transactional
    public EmployeeResponse register(EmployeeRequest req) {
        if (userRepository.existsByEmployeeNo(req.getEmployeeNo())) {
            throw new IllegalArgumentException("이미 존재하는 사원번호입니다: " + req.getEmployeeNo());
        }
        User user = User.builder()
                .employeeNo(req.getEmployeeNo())
                .rawPassword(req.getEmployeeNo())
                .name(req.getName())
                .department(req.getDepartment())
                .role(req.toRole())
                .encoder(passwordEncoder)
                .build();
        return new EmployeeResponse(userRepository.save(user));
    }

    @Transactional
    public EmployeeResponse update(Long id, EmployeeRequest req) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("직원을 찾을 수 없습니다."));
        user.updateInfo(req.getName(), req.getDepartment(), req.toRole());
        return new EmployeeResponse(user);
    }

    @Transactional
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("직원을 찾을 수 없습니다.");
        }
        userRepository.deleteById(id);
    }

    @Transactional
    public List<EmployeeResponse> registerByExcel(MultipartFile file) throws IOException {
        List<EmployeeResponse> results = new ArrayList<>();
        try (Workbook wb = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = wb.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                String employeeNo  = cellStr(row.getCell(0));
                String name        = cellStr(row.getCell(1));
                String department  = cellStr(row.getCell(2));
                int    roleOrdinal = (int) row.getCell(3).getNumericCellValue();

                if (employeeNo.isBlank() || name.isBlank()) continue;
                if (userRepository.existsByEmployeeNo(employeeNo)) continue;

                User user = User.builder()
                        .employeeNo(employeeNo)
                        .rawPassword(employeeNo)
                        .name(name)
                        .department(department)
                        .role(User.Role.values()[roleOrdinal])
                        .encoder(passwordEncoder)
                        .build();
                results.add(new EmployeeResponse(userRepository.save(user)));
            }
        }
        return results;
    }

    private String cellStr(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING  -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            default      -> "";
        };
    }
}
