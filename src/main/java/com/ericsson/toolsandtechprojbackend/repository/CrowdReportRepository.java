package com.ericsson.toolsandtechprojbackend.repository;

import com.ericsson.toolsandtechprojbackend.entity.CrowdReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CrowdReportRepository extends JpaRepository<CrowdReport, Long> {
    List<CrowdReport> findTop20ByOrderBySubmittedAtDesc();
}
