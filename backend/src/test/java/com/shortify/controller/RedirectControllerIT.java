package com.shortify.controller;

import com.shortify.entity.Role;
import com.shortify.entity.Url;
import com.shortify.entity.User;
import com.shortify.repository.UrlRepository;
import com.shortify.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RedirectControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UrlRepository urlRepository;

    @BeforeEach
    void setUp() {
        urlRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("GET /{shortCode} - Should return 302 Found redirect to original URL")
    void testRedirectSuccess() throws Exception {
        User user = userRepository.save(User.builder()
                .name("Redirect User")
                .email("redirect@example.com")
                .password("password")
                .role(Role.ROLE_USER)
                .build());

        Url url = urlRepository.save(Url.builder()
                .user(user)
                .originalUrl("https://spring.io")
                .shortCode("spring302")
                .clickCount(0L)
                .isActive(true)
                .build());

        mockMvc.perform(get("/spring302"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "https://spring.io"));
    }

    @Test
    @DisplayName("GET /{shortCode} - Should return 404 Not Found for non-existing short code")
    void testRedirectNotFound() throws Exception {
        mockMvc.perform(get("/nonExistingCode99"))
                .andExpect(status().isNotFound());
    }
}
