package com.ericsson.toolsandtechprojbackend.service;

import com.ericsson.toolsandtechprojbackend.dto.FestivalAreaResponse;
import com.ericsson.toolsandtechprojbackend.entity.FestivalArea;
import com.ericsson.toolsandtechprojbackend.repository.CrowdReportRepository;
import com.ericsson.toolsandtechprojbackend.repository.FestivalAreaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FestivalAreaService {

    private final FestivalAreaRepository festivalAreaRepository;
    private final CrowdReportRepository crowdReportRepository;

    public FestivalArea createArea(FestivalArea area) {
        return festivalAreaRepository.save(area);
    }

    public List<FestivalAreaResponse> getAllAreas() {
        return festivalAreaRepository.findAll().stream()
                .map(area -> new FestivalAreaResponse(
                        area,
                        crowdReportRepository.findTopByAreaOrderBySubmittedAtDesc(area).orElse(null)
                ))
                .toList();
    }
}
