package com.ericsson.toolsandtechprojbackend.dto;

import com.ericsson.toolsandtechprojbackend.entity.CrowdLevel;
import com.ericsson.toolsandtechprojbackend.entity.CrowdReport;
import com.ericsson.toolsandtechprojbackend.entity.FestivalArea;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CrowdReportResponse {
    private Long id;
    private FestivalArea area;
    private CrowdLevel level;
    private String note;
    private LocalDateTime submittedAt;

    public CrowdReportResponse(CrowdReport report) {
        this.id = report.getId();
        this.area = report.getArea();
        this.level = report.getLevel();
        this.note = report.getNote();
        this.submittedAt = report.getSubmittedAt();
    }
}
