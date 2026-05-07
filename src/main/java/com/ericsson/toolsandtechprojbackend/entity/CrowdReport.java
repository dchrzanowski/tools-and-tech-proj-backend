package com.ericsson.toolsandtechprojbackend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class CrowdReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "area_id")
    private FestivalArea area;

    @Enumerated(EnumType.STRING)
    private CrowdLevel level;

    private String note;
    private LocalDateTime submittedAt;
}
