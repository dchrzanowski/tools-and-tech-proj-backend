package com.ericsson.toolsandtechprojbackend.dto;

import com.ericsson.toolsandtechprojbackend.entity.CrowdLevel;
import lombok.Data;

@Data
public class CrowdReportRequest {
    private Long areaId;
    private CrowdLevel level;
    private String note;
}
