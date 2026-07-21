package com.shortify.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shortify.dto.request.RegisterRequest;
import com.shortify.dto.response.AuthResponse;
import com.shortify.repository.UrlClickRepository;
import com.shortify.repository.UrlRepository;
import com.shortify.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AnalyticsControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UrlClickRepository urlClickRepository;

    @Autowired
    private UrlRepository urlRepository;

    @Autowired
    private UserRepository userRepository;

    private String jwtToken;

    @BeforeEach
    void setUp() throws Exception {
        urlClickRepository.deleteAll();
        urlRepository.deleteAll();
        userRepository.deleteAll();

        RegisterRequest registerReq = RegisterRequest.builder()
                .name("Analytics IT User")
                .email("analyticsuser@example.com")
                .password("password123")
                .build();

        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerReq)))
                .andReturn();

        String json = result.getResponse().getContentAsString();
        AuthResponse authRes = objectMapper.readTree(json).get("data").traverse(objectMapper).readValueAs(AuthResponse.class);
        jwtToken = authRes.getAccessToken();
    }

    @Test
    @DisplayName("GET /api/analytics/dashboard - Should retrieve aggregated analytics for authenticated user")
    void testGetDashboardAnalytics() throws Exception {
        mockMvc.perform(get("/api/analytics/dashboard")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalUrls").exists())
                .andExpect(jsonPath("$.data.totalClicks").exists());
    }
}
