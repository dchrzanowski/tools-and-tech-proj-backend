package com.ericsson.toolsandtechprojbackend.dto;

import com.ericsson.toolsandtechprojbackend.entity.CrowdReport;
import com.ericsson.toolsandtechprojbackend.entity.FestivalArea;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FestivalAreaResponse {
    private FestivalArea area;
    private CrowdReport latestReport;
}
