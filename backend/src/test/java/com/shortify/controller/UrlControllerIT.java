package com.shortify.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shortify.dto.request.RegisterRequest;
import com.shortify.dto.request.UrlCreateRequest;
import com.shortify.dto.response.AuthResponse;

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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class UrlControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UrlRepository urlRepository;

    private String jwtToken;

    @BeforeEach
    void setUp() throws Exception {
        urlRepository.deleteAll();
        userRepository.deleteAll();

        // Register user and obtain token
        RegisterRequest registerReq = RegisterRequest.builder()
                .name("Url IT User")
                .email("urluser@example.com")
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
    @DisplayName("POST /api/urls - Should create short URL when authenticated")
    void testCreateShortUrl() throws Exception {
        UrlCreateRequest request = UrlCreateRequest.builder()
                .originalUrl("https://example.com/integration-test")
                .customAlias("myAlias123")
                .build();

        mockMvc.perform(post("/api/urls")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.shortCode").value("myAlias123"))
                .andExpect(jsonPath("$.data.originalUrl").value("https://example.com/integration-test"));
    }

    @Test
    @DisplayName("GET /api/urls - Should retrieve paginated list of URLs")
    void testGetUrls() throws Exception {
        mockMvc.perform(get("/api/urls")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray());
    }
}
