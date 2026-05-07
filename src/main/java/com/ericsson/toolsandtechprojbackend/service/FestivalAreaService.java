package com.ericsson.toolsandtechprojbackend.service;

import com.ericsson.toolsandtechprojbackend.entity.FestivalArea;
import com.ericsson.toolsandtechprojbackend.repository.FestivalAreaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FestivalAreaService {

    private final FestivalAreaRepository festivalAreaRepository;

    public FestivalArea createArea(FestivalArea area) {
        return festivalAreaRepository.save(area);
    }

    public List<FestivalArea> getAllAreas() {
        return festivalAreaRepository.findAll();
    }
}
