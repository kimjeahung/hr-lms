package com.hr.backend.domain.enrollment.service;

import com.hr.backend.admin.dto.EnrollmentResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EnrollmentExcelService {

    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final String[] HEADERS = {
            "수강 ID", "직원 ID", "직원명", "부서", "강좌명", "차수",
            "진행률(%)", "승인상태", "수강상태", "수강신청일", "완료일"
    };

    /**
     * 이수 현황 목록을 Excel(.xlsx)로 변환하여 byte[] 반환
     */
    public byte[] export(List<EnrollmentResponse> enrollments) {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("이수현황");

            // ── 헤더 스타일
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(BorderStyle.THIN);

            // ── 헤더 행
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < HEADERS.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(HEADERS[i]);
                cell.setCellStyle(headerStyle);
                sheet.setColumnWidth(i, 18 * 256);  // 기본 열 너비
            }
            sheet.setColumnWidth(3, 22 * 256);  // 부서
            sheet.setColumnWidth(4, 40 * 256);  // 강좌명

            // ── 데이터 행
            int rowNum = 1;
            for (EnrollmentResponse e : enrollments) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(e.getEnrollmentId());
                row.createCell(1).setCellValue(e.getUserId());
                row.createCell(2).setCellValue(e.getUserName());
                row.createCell(3).setCellValue(e.getDepartment());
                row.createCell(4).setCellValue(e.getCourseTitle());
                row.createCell(5).setCellValue(e.getRoundNo() + "차");
                row.createCell(6).setCellValue(e.getProgress());
                row.createCell(7).setCellValue(translateApproval(e.getApprovalStatus()));
                row.createCell(8).setCellValue(translateStatus(e.getStatus()));
                row.createCell(9).setCellValue(
                        e.getEnrolledAt() != null ? e.getEnrolledAt().format(DT_FMT) : "");
                row.createCell(10).setCellValue(
                        e.getCompletedAt() != null ? e.getCompletedAt().format(DT_FMT) : "");
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("Excel 파일 생성 중 오류가 발생했습니다.", e);
        }
    }

    private String translateStatus(String status) {
        return switch (status) {
            case "NOT_STARTED" -> "미시작";
            case "IN_PROGRESS" -> "진행중";
            case "DONE"        -> "이수완료";
            default            -> status;
        };
    }

    private String translateApproval(String status) {
        return switch (status) {
            case "PENDING"  -> "대기";
            case "APPROVED" -> "승인";
            case "REJECTED" -> "반려";
            default         -> status;
        };
    }
}
