package com.ericsson.toolsandtechprojbackend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class FestivalArea {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private String location;
    private String type;
}
