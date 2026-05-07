package com.ericsson.toolsandtechprojbackend.controller;

import com.ericsson.toolsandtechprojbackend.dto.CrowdReportRequest;
import com.ericsson.toolsandtechprojbackend.dto.CrowdReportResponse;
import com.ericsson.toolsandtechprojbackend.entity.CrowdReport;
import com.ericsson.toolsandtechprojbackend.service.CrowdReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class CrowdReportController {

    private final CrowdReportService crowdReportService;

    @PostMapping
    public ResponseEntity<?> submitReport(@RequestBody CrowdReportRequest request) {
        try {
            return ResponseEntity.ok(crowdReportService.submitReport(request.getAreaId(), request.getLevel(), request.getNote()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public List<CrowdReportResponse> getRecentReports() {
        return crowdReportService.getRecentReports();
    }
}
