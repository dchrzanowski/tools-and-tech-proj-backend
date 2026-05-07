package com.ericsson.toolsandtechprojbackend.service;

import com.ericsson.toolsandtechprojbackend.entity.*;
import com.ericsson.toolsandtechprojbackend.repository.CrowdAlertRepository;
import com.ericsson.toolsandtechprojbackend.repository.CrowdReportRepository;
import com.ericsson.toolsandtechprojbackend.repository.FestivalAreaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CrowdReportService {

    private final CrowdReportRepository crowdReportRepository;
    private final FestivalAreaRepository festivalAreaRepository;
    private final CrowdAlertRepository crowdAlertRepository;

    public CrowdReport submitReport(Long areaId, CrowdLevel level, String note) {
        FestivalArea area = festivalAreaRepository.findById(areaId)
                .orElseThrow(() -> new RuntimeException("Area not found: " + areaId));

        CrowdReport report = new CrowdReport();
        report.setArea(area);
        report.setLevel(level);
        report.setNote(note);
        report.setSubmittedAt(LocalDateTime.now());
        crowdReportRepository.save(report);

        if (level == CrowdLevel.FULL) {
            CrowdAlert alert = new CrowdAlert();
            alert.setArea(area);
            alert.setMessage(area.getName() + " is at full capacity");
            alert.setStatus(AlertStatus.ACTIVE);
            alert.setCreatedAt(LocalDateTime.now());
            crowdAlertRepository.save(alert);
        }

        return report;
    }

    public List<CrowdReport> getRecentReports() {
        return crowdReportRepository.findTop20ByOrderBySubmittedAtDesc();
    }
}
