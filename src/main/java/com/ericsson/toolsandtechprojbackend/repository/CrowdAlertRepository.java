package com.ericsson.toolsandtechprojbackend.repository;

import com.ericsson.toolsandtechprojbackend.entity.AlertStatus;
import com.ericsson.toolsandtechprojbackend.entity.CrowdAlert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CrowdAlertRepository extends JpaRepository<CrowdAlert, Long> {
    List<CrowdAlert> findByStatus(AlertStatus status);
}
