package com.example.skip.controller.RentAdmin;

import com.example.skip.service.RentDashboardService;
import com.example.skip.service.RentExcelExportService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rentAdmin")
public class RentDashboardController {
    @Autowired
    private RentDashboardService rentDashboardService;
    @Autowired
    private RentExcelExportService rentExcelExportService;

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getSummary(
            @RequestParam Long userId,
            @RequestParam(required = false) Long rentId,
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        Map<String, Object> summary = rentDashboardService.getSummary(userId, rentId, start, end);
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/sales/chart")
    public ResponseEntity<List<Map<String, Object>>> getChart(
            @RequestParam Long userId,
            @RequestParam(required = false) Long rentId,
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return ResponseEntity.ok(rentDashboardService.getSalesChartData(userId, rentId, start, end));
    }

    @GetMapping("/summary/export")
    public void exportExcel(
            @RequestParam Long userId,
            @RequestParam(required = false) Long rentId,
            @RequestParam("extension") String extension,
            @RequestParam("atStart") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam("atEnd") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            HttpServletResponse response
    ) throws IOException {
        String fileName = "매출전표_" + start + "_" + end + extension;
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replaceAll("\\+","%20");
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encodedFileName);

        rentExcelExportService.writeExcelForUser(response.getOutputStream(), userId, rentId, start.toString(), end.toString());
    }
}