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
class CrowdReportControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void submitReport_validRequest_returns200() throws Exception {
        mockMvc.perform(post("/reports")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "areaId": 2,
                            "level": "FULL",
                            "note": "Completely packed"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.level", equalTo("FULL")))
                .andExpect(jsonPath("$.note", equalTo("Completely packed")));
    }

    @Test
    void submitReport_duplicateLevel_returns400() throws Exception {
        mockMvc.perform(post("/reports")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "areaId": 1,
                            "level": "FULL",
                            "note": "Still packed"
                        }
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("already reported as FULL")));
    }

    @Test
    void submitReport_areaNotFound_returns400() throws Exception {
        mockMvc.perform(post("/reports")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "areaId": 99999,
                            "level": "LOW",
                            "note": "test"
                        }
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Area not found")));
    }

    @Test
    void getRecentReports_returns200() throws Exception {
        mockMvc.perform(get("/reports"))
                .andExpect(status().isOk());
    }

    @Test
    void getRecentReports_containsSeededData() throws Exception {
        mockMvc.perform(get("/reports"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", not(empty())))
                .andExpect(jsonPath("$.length()", lessThanOrEqualTo(20)));
    }

    @Test
    void getRecentReports_eachReportContainsArea() throws Exception {
        mockMvc.perform(get("/reports"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].area", everyItem(notNullValue())));
    }
}
