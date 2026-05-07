package com.ericsson.toolsandtechprojbackend.repository;

import com.ericsson.toolsandtechprojbackend.entity.AlertStatus;
import com.ericsson.toolsandtechprojbackend.entity.CrowdAlert;
import com.ericsson.toolsandtechprojbackend.entity.FestivalArea;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CrowdAlertRepository extends JpaRepository<CrowdAlert, Long> {
    List<CrowdAlert> findByStatus(AlertStatus status);
    Optional<CrowdAlert> findByAreaAndStatus(FestivalArea area, AlertStatus status);
}
