package com.sweetypie.sweetypie.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sweetypie.sweetypie.dto.LoginDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;


import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = "spring.config.location="
        + "classpath:test.yml")
@Transactional
class AuthControllerTest {

    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    void before(WebApplicationContext was) {
        mockMvc = MockMvcBuilders.webAppContextSetup(was)
                .alwaysDo(print())
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .build();
    }

    @DisplayName("로그인")
    @Test
    void loginTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/login")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                        objectMapper.writeValueAsString(
                                LoginDto.builder()
                                .email("test123@gmail.com")
                                .password("12345678a!")
                                .build()
                        )
                ))
                .andExpect(status().isOk());
    }

    @DisplayName("로그인 -탈퇴된 회원")
    @Test
    void withdrawalMemberLoginTest() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.post("/api/login")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                        objectMapper.writeValueAsString(
                                LoginDto.builder()
                                        .email("withdrawaltest@gmail.com")
                                        .password("12345678a!")
                                        .build()
                        )
                ))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("일반로그인 - 구글회원")
    @Test
    void googleMemberTryNormalLoginTest() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.post("/api/login")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                        objectMapper.writeValueAsString(
                                LoginDto.builder()
                                        .email("google@gmail.com")
                                        .password("12345678a!")
                                        .build()
                        )
                ))
                .andExpect(status().isBadRequest());
    }

}