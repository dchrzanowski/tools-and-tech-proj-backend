package com.ericsson.toolsandtechprojbackend.controller;

import com.ericsson.toolsandtechprojbackend.entity.CrowdAlert;
import com.ericsson.toolsandtechprojbackend.service.CrowdAlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/alerts")
@RequiredArgsConstructor
public class CrowdAlertController {

    private final CrowdAlertService crowdAlertService;

    @GetMapping
    public List<CrowdAlert> getActiveAlerts() {
        return crowdAlertService.getActiveAlerts();
    }
}
