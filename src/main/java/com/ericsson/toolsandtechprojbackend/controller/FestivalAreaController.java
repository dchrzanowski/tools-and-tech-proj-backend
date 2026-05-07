package com.ericsson.toolsandtechprojbackend.controller;

import com.ericsson.toolsandtechprojbackend.entity.FestivalArea;
import com.ericsson.toolsandtechprojbackend.service.FestivalAreaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/areas")
@RequiredArgsConstructor
public class FestivalAreaController {

    private final FestivalAreaService festivalAreaService;

    @PostMapping
    public FestivalArea createArea(@RequestBody FestivalArea area) {
        return festivalAreaService.createArea(area);
    }

    @GetMapping
    public List<FestivalArea> getAllAreas() {
        return festivalAreaService.getAllAreas();
    }
}
