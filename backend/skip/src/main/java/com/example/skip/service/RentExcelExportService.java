package com.example.skip.service;

import com.example.skip.dto.admin.AdminDetailDTO;
import com.example.skip.repository.AdminReportRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RentExcelExportService {
    private final AdminReportRepository reportRepo;

    public void writeExcelForUser(OutputStream os, Long userId, Long rentId, String startDate, String endDate) {
        LocalDate sd = LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE);
        LocalDate ed = LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE);
        LocalDateTime start = sd.atStartOfDay();
        LocalDateTime end = ed.atTime(LocalTime.MAX);

        List<AdminDetailDTO> details = (rentId != null)
                ? reportRepo.findAllDetailsByUserAndRentBetween(userId, rentId, start, end)
                : reportRepo.findAllDetailsByUserBetween(userId, start, end);

        try (SXSSFWorkbook wb = new SXSSFWorkbook()) {
            Sheet detailSheet = wb.createSheet("Details");
            createDetailSheet(detailSheet, wb, details);
            wb.write(os);
            os.flush();
        } catch (Exception ex) {
            throw new RuntimeException("Excel 생성 실패", ex);
        }
    }

    private void createDetailSheet(Sheet sheet, Workbook wb, List<AdminDetailDTO> list) {
        CellStyle headerStyle = wb.createCellStyle();
        Font headerFont = wb.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);

        String[] headers = {
                "결제ID", "PG주문번호", "PG결제ID", "렌탈샵명", "회원ID",
                "총결제금액", "수수료율", "결제수단", "PG사", "상태", "결제일시"
        };
        Row h = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell c = h.createCell(i);
            c.setCellValue(headers[i]);
            c.setCellStyle(headerStyle);
            sheet.setColumnWidth(i, 5000);
        }

        CellStyle moneyStyle = wb.createCellStyle();
        moneyStyle.setDataFormat(wb.createDataFormat().getFormat("#,##0"));

        CellStyle datetimeStyle = wb.createCellStyle();
        datetimeStyle.setDataFormat(
                wb.createDataFormat().getFormat("yyyy-mm-dd hh:mm:ss")
        );

        int rowIdx = 1;
        for (AdminDetailDTO dto : list) {
            Row r = sheet.createRow(rowIdx++);
            r.createCell(0).setCellValue(dto.getPaymentId());
            r.createCell(1).setCellValue(dto.getMerchantUid());
            r.createCell(2).setCellValue(dto.getImpUid());
            r.createCell(3).setCellValue(dto.getRentName());
            r.createCell(4).setCellValue(dto.getPaymentUserId());

            Cell priceCell = r.createCell(5);
            priceCell.setCellValue(dto.getTotalPrice());
            priceCell.setCellStyle(moneyStyle);

            Cell rateCell = r.createCell(6);
            rateCell.setCellValue(dto.getCommissionRate());

            r.createCell(7).setCellValue(dto.getMethod());
            r.createCell(8).setCellValue(dto.getPgProvider());
            r.createCell(9).setCellValue(dto.getStatus().name());

            Cell dtCell = r.createCell(10);
            dtCell.setCellValue(dto.getCreatedAt());
            dtCell.setCellStyle(datetimeStyle);
        }
    }
}