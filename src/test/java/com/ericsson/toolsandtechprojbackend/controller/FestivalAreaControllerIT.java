package com.ericsson.toolsandtechprojbackend.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class FestivalAreaControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createArea_validRequest_returns200() throws Exception {
        mockMvc.perform(post("/areas")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "name": "Comedy Tent",
                            "description": "Stand-up comedy",
                            "location": "West Field",
                            "type": "Entertainment"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", equalTo("Comedy Tent")))
                .andExpect(jsonPath("$.location", equalTo("West Field")))
                .andExpect(jsonPath("$.type", equalTo("Entertainment")));
    }

    @Test
    void getAllAreas_returns200() throws Exception {
        mockMvc.perform(get("/areas"))
                .andExpect(status().isOk());
    }

    @Test
    void getAllAreas_containsSeededAreas() throws Exception {
        mockMvc.perform(get("/areas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", not(empty())))
                .andExpect(jsonPath("$[*].area.name", hasItems("Main Stage", "Food Village", "Craft Beer Bar", "First Aid Point")));
    }

    @Test
    void getAllAreas_eachResponseHasAreaField() throws Exception {
        mockMvc.perform(get("/areas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].area", everyItem(notNullValue())));
    }

    @Test
    void getAllAreas_areaWithReports_hasLatestReport() throws Exception {
        mockMvc.perform(get("/areas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.area.name == 'Main Stage')].latestReport.level", hasItem("FULL")));
    }

    @Test
    void getAllAreas_areaWithNoReports_hasNullLatestReport() throws Exception {
        mockMvc.perform(get("/areas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.area.name == 'First Aid Point')].latestReport", hasItem(nullValue())));
    }
}
