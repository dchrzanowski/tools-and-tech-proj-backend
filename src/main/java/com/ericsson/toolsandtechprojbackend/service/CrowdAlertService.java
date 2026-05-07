package com.ericsson.toolsandtechprojbackend.service;

import com.ericsson.toolsandtechprojbackend.entity.AlertStatus;
import com.ericsson.toolsandtechprojbackend.entity.CrowdAlert;
import com.ericsson.toolsandtechprojbackend.repository.CrowdAlertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CrowdAlertService {

    private final CrowdAlertRepository crowdAlertRepository;

    public List<CrowdAlert> getActiveAlerts() {
        return crowdAlertRepository.findByStatus(AlertStatus.ACTIVE);
    }

    public CrowdAlert resolveAlert(Long id) {
        CrowdAlert alert = crowdAlertRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alert not found: " + id));
        alert.setStatus(AlertStatus.RESOLVED);
        return crowdAlertRepository.save(alert);
    }
}
