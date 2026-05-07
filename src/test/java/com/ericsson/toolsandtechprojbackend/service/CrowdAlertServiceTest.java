package com.ericsson.toolsandtechprojbackend.service;

import com.ericsson.toolsandtechprojbackend.entity.AlertStatus;
import com.ericsson.toolsandtechprojbackend.entity.CrowdAlert;
import com.ericsson.toolsandtechprojbackend.repository.CrowdAlertRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CrowdAlertServiceTest {

    @Mock
    private CrowdAlertRepository crowdAlertRepository;

    @InjectMocks
    private CrowdAlertService crowdAlertService;

    @Test
    void getActiveAlerts_returnsActiveAlerts() {
        CrowdAlert alert = new CrowdAlert();
        alert.setStatus(AlertStatus.ACTIVE);

        when(crowdAlertRepository.findByStatus(AlertStatus.ACTIVE)).thenReturn(List.of(alert));

        List<CrowdAlert> result = crowdAlertService.getActiveAlerts();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(AlertStatus.ACTIVE);
    }

    @Test
    void getActiveAlerts_noAlerts_returnsEmptyList() {
        when(crowdAlertRepository.findByStatus(AlertStatus.ACTIVE)).thenReturn(List.of());

        List<CrowdAlert> result = crowdAlertService.getActiveAlerts();

        assertThat(result).isEmpty();
    }
}
