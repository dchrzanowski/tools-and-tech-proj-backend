package com.ericsson.toolsandtechprojbackend.service;

import com.ericsson.toolsandtechprojbackend.dto.CrowdReportResponse;
import com.ericsson.toolsandtechprojbackend.entity.*;
import com.ericsson.toolsandtechprojbackend.repository.CrowdAlertRepository;
import com.ericsson.toolsandtechprojbackend.repository.CrowdReportRepository;
import com.ericsson.toolsandtechprojbackend.repository.FestivalAreaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CrowdReportServiceTest {

    @Mock
    private CrowdReportRepository crowdReportRepository;
    @Mock
    private FestivalAreaRepository festivalAreaRepository;
    @Mock
    private CrowdAlertRepository crowdAlertRepository;

    @InjectMocks
    private CrowdReportService crowdReportService;

    private FestivalArea mockArea() {
        FestivalArea area = new FestivalArea();
        area.setId(1L);
        area.setName("Main Stage");
        return area;
    }

    @Test
    void submitReport_areaNotFound_throwsException() {
        when(festivalAreaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> crowdReportService.submitReport(1L, CrowdLevel.LOW, "note"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Area not found");
    }

    @Test
    void submitReport_duplicateLevel_throwsException() {
        FestivalArea area = mockArea();
        CrowdReport latest = new CrowdReport();
        latest.setLevel(CrowdLevel.FULL);

        when(festivalAreaRepository.findById(1L)).thenReturn(Optional.of(area));
        when(crowdReportRepository.findTopByAreaOrderBySubmittedAtDesc(area)).thenReturn(Optional.of(latest));

        assertThatThrownBy(() -> crowdReportService.submitReport(1L, CrowdLevel.FULL, "note"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("already reported as FULL");
    }

    @Test
    void submitReport_fullLevel_createsActiveAlert() {
        FestivalArea area = mockArea();

        when(festivalAreaRepository.findById(1L)).thenReturn(Optional.of(area));
        when(crowdReportRepository.findTopByAreaOrderBySubmittedAtDesc(area)).thenReturn(Optional.empty());
        when(crowdReportRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        crowdReportService.submitReport(1L, CrowdLevel.FULL, "packed");

        verify(crowdAlertRepository).save(argThat(alert ->
                alert.getStatus() == AlertStatus.ACTIVE &&
                alert.getArea().equals(area)
        ));
    }

    @Test
    void submitReport_nonFullLevel_resolvesExistingAlert() {
        FestivalArea area = mockArea();
        CrowdAlert existingAlert = new CrowdAlert();
        existingAlert.setStatus(AlertStatus.ACTIVE);

        when(festivalAreaRepository.findById(1L)).thenReturn(Optional.of(area));
        when(crowdReportRepository.findTopByAreaOrderBySubmittedAtDesc(area)).thenReturn(Optional.empty());
        when(crowdReportRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(crowdAlertRepository.findByAreaAndStatus(area, AlertStatus.ACTIVE)).thenReturn(Optional.of(existingAlert));

        crowdReportService.submitReport(1L, CrowdLevel.LOW, "quieter now");

        verify(crowdAlertRepository).save(argThat(alert -> alert.getStatus() == AlertStatus.RESOLVED));
    }

    @Test
    void submitReport_nonFullLevel_noExistingAlert_doesNotSaveAlert() {
        FestivalArea area = mockArea();

        when(festivalAreaRepository.findById(1L)).thenReturn(Optional.of(area));
        when(crowdReportRepository.findTopByAreaOrderBySubmittedAtDesc(area)).thenReturn(Optional.empty());
        when(crowdReportRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(crowdAlertRepository.findByAreaAndStatus(area, AlertStatus.ACTIVE)).thenReturn(Optional.empty());

        crowdReportService.submitReport(1L, CrowdLevel.MEDIUM, "moderate");

        verify(crowdAlertRepository, never()).save(any());
    }

    @Test
    void submitReport_success_savesReportWithCorrectFields() {
        FestivalArea area = mockArea();

        when(festivalAreaRepository.findById(1L)).thenReturn(Optional.of(area));
        when(crowdReportRepository.findTopByAreaOrderBySubmittedAtDesc(area)).thenReturn(Optional.empty());
        when(crowdReportRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        CrowdReport result = crowdReportService.submitReport(1L, CrowdLevel.MEDIUM, "busy");

        assertThat(result.getLevel()).isEqualTo(CrowdLevel.MEDIUM);
        assertThat(result.getNote()).isEqualTo("busy");
        assertThat(result.getArea()).isEqualTo(area);
        assertThat(result.getSubmittedAt()).isNotNull();
    }

    @Test
    void getRecentReports_returnsMappedDtos() {
        FestivalArea area = mockArea();
        CrowdReport report = new CrowdReport();
        report.setId(1L);
        report.setArea(area);
        report.setLevel(CrowdLevel.LOW);
        report.setNote("quiet");
        report.setSubmittedAt(LocalDateTime.now());

        when(crowdReportRepository.findTop20ByOrderBySubmittedAtDesc()).thenReturn(List.of(report));

        List<CrowdReportResponse> result = crowdReportService.getRecentReports();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getLevel()).isEqualTo(CrowdLevel.LOW);
        assertThat(result.get(0).getNote()).isEqualTo("quiet");
        assertThat(result.get(0).getArea()).isEqualTo(area);
    }
}
