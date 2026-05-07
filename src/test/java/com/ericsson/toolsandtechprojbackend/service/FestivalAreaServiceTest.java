package com.ericsson.toolsandtechprojbackend.service;

import com.ericsson.toolsandtechprojbackend.dto.FestivalAreaResponse;
import com.ericsson.toolsandtechprojbackend.entity.CrowdLevel;
import com.ericsson.toolsandtechprojbackend.entity.CrowdReport;
import com.ericsson.toolsandtechprojbackend.entity.FestivalArea;
import com.ericsson.toolsandtechprojbackend.repository.CrowdReportRepository;
import com.ericsson.toolsandtechprojbackend.repository.FestivalAreaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FestivalAreaServiceTest {

    @Mock
    private FestivalAreaRepository festivalAreaRepository;
    @Mock
    private CrowdReportRepository crowdReportRepository;

    @InjectMocks
    private FestivalAreaService festivalAreaService;

    private FestivalArea mockArea(Long id, String name) {
        FestivalArea area = new FestivalArea();
        area.setId(id);
        area.setName(name);
        return area;
    }

    @Test
    void createArea_savesAndReturnsArea() {
        FestivalArea area = mockArea(1L, "Main Stage");
        when(festivalAreaRepository.save(area)).thenReturn(area);

        FestivalArea result = festivalAreaService.createArea(area);

        assertThat(result.getName()).isEqualTo("Main Stage");
        verify(festivalAreaRepository).save(area);
    }

    @Test
    void getAllAreas_withLatestReport_returnsPopulatedResponse() {
        FestivalArea area = mockArea(1L, "Main Stage");
        CrowdReport report = new CrowdReport();
        report.setLevel(CrowdLevel.FULL);

        when(festivalAreaRepository.findAll()).thenReturn(List.of(area));
        when(crowdReportRepository.findTopByAreaOrderBySubmittedAtDesc(area)).thenReturn(Optional.of(report));

        List<FestivalAreaResponse> result = festivalAreaService.getAllAreas();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getArea()).isEqualTo(area);
        assertThat(result.get(0).getLatestReport().getLevel()).isEqualTo(CrowdLevel.FULL);
    }

    @Test
    void getAllAreas_noReport_returnsNullLatestReport() {
        FestivalArea area = mockArea(1L, "Main Stage");

        when(festivalAreaRepository.findAll()).thenReturn(List.of(area));
        when(crowdReportRepository.findTopByAreaOrderBySubmittedAtDesc(area)).thenReturn(Optional.empty());

        List<FestivalAreaResponse> result = festivalAreaService.getAllAreas();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getLatestReport()).isNull();
    }

    @Test
    void getAllAreas_multipleAreas_allMappedCorrectly() {
        FestivalArea area1 = mockArea(1L, "Main Stage");
        FestivalArea area2 = mockArea(2L, "Food Village");
        CrowdReport report = new CrowdReport();
        report.setLevel(CrowdLevel.MEDIUM);

        when(festivalAreaRepository.findAll()).thenReturn(List.of(area1, area2));
        when(crowdReportRepository.findTopByAreaOrderBySubmittedAtDesc(area1)).thenReturn(Optional.of(report));
        when(crowdReportRepository.findTopByAreaOrderBySubmittedAtDesc(area2)).thenReturn(Optional.empty());

        List<FestivalAreaResponse> result = festivalAreaService.getAllAreas();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getLatestReport().getLevel()).isEqualTo(CrowdLevel.MEDIUM);
        assertThat(result.get(1).getLatestReport()).isNull();
    }
}
