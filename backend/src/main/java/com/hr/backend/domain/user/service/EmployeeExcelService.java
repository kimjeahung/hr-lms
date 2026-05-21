package com.hr.backend.domain.user.service;

import com.hr.backend.admin.dto.EmployeeResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 직원 목록 Excel(.xlsx) 내보내기 서비스.
 */
@Service
@RequiredArgsConstructor
public class EmployeeExcelService {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DT_FMT   = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private static final String[] HEADERS = {
            "직원 ID", "사번", "이름", "부서", "직급",
            "고용형태", "이메일", "연락처", "입사일", "역할", "재직상태", "등록일"
    };

    public byte[] export(List<EmployeeResponse> employees) {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("직원목록");

            // 헤더 스타일
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.CORNFLOWER_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(BorderStyle.THIN);

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < HEADERS.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(HEADERS[i]);
                cell.setCellStyle(headerStyle);
                sheet.setColumnWidth(i, 16 * 256);
            }
            // 이름·부서·이메일 열 너비 확대
            sheet.setColumnWidth(2, 14 * 256);
            sheet.setColumnWidth(3, 22 * 256);
            sheet.setColumnWidth(6, 30 * 256);

            int rowNum = 1;
            for (EmployeeResponse e : employees) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(e.getUserId());
                row.createCell(1).setCellValue(e.getEmployeeNo());
                row.createCell(2).setCellValue(e.getName());
                row.createCell(3).setCellValue(e.getDepartmentName() != null ? e.getDepartmentName() : "");
                row.createCell(4).setCellValue(e.getPosition() != null ? e.getPosition() : "");
                row.createCell(5).setCellValue(e.getEmpType() == 1 ? "현장직" : "사무직");
                row.createCell(6).setCellValue(e.getEmail() != null ? e.getEmail() : "");
                row.createCell(7).setCellValue(e.getPhone() != null ? e.getPhone() : "");
                row.createCell(8).setCellValue(e.getHireDate() != null ? e.getHireDate().format(DATE_FMT) : "");
                row.createCell(9).setCellValue(translateRole(e.getRole()));
                row.createCell(10).setCellValue(e.isActive() ? "재직" : "퇴직");
                row.createCell(11).setCellValue(e.getCreatedAt() != null ? e.getCreatedAt().format(DT_FMT) : "");
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("직원 목록 Excel 생성 중 오류가 발생했습니다.", e);
        }
    }

    private String translateRole(String role) {
        if (role == null) return "";
        return switch (role) {
            case "ROLE_ADMIN" -> "관리자";
            case "ROLE_USER"  -> "일반직원";
            default           -> role;
        };
    }
}
