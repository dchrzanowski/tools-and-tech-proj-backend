package com.ericsson.toolsandtechprojbackend.controller;

import com.ericsson.toolsandtechprojbackend.dto.CrowdReportRequest;
import com.ericsson.toolsandtechprojbackend.entity.CrowdReport;
import com.ericsson.toolsandtechprojbackend.service.CrowdReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class CrowdReportController {

    private final CrowdReportService crowdReportService;

    @PostMapping
    public CrowdReport submitReport(@RequestBody CrowdReportRequest request) {
        return crowdReportService.submitReport(request.getAreaId(), request.getLevel(), request.getNote());
    }

    @GetMapping
    public List<CrowdReport> getRecentReports() {
        return crowdReportService.getRecentReports();
    }
}
