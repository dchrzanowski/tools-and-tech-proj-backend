package com.ericsson.toolsandtechprojbackend.repository;

import com.ericsson.toolsandtechprojbackend.entity.CrowdReport;
import com.ericsson.toolsandtechprojbackend.entity.FestivalArea;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CrowdReportRepository extends JpaRepository<CrowdReport, Long> {
    List<CrowdReport> findTop20ByOrderBySubmittedAtDesc();
    Optional<CrowdReport> findTopByAreaOrderBySubmittedAtDesc(FestivalArea area);
}
