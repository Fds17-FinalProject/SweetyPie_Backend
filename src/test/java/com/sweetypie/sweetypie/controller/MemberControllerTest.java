package com.sweetypie.sweetypie.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sweetypie.sweetypie.dto.LoginDto;
import com.sweetypie.sweetypie.dto.MemberDto;
import com.sweetypie.sweetypie.service.AuthService;
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

import java.time.LocalDate;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest(properties = "spring.config.location="
        + "classpath:test.yml")
class MemberControllerTest {

    private MockMvc mockMvc;

    private String token;

    @Autowired
    AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void before(WebApplicationContext wac) {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .addFilter(new CharacterEncodingFilter("UTF-8", true))
                .alwaysDo(print())
                .build();
    }
    @BeforeEach
    void getToken() {
        LoginDto loginDto = new LoginDto();
        loginDto.setEmail("test123@gmail.com");
        loginDto.setPassword("12345678a!");

        token = authService.login(loginDto);
    }

    @DisplayName("회원정보검색")
    @Test
    void getMemberTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/member")
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test123@gmail.com"));
    }

    @DisplayName("회원가입")
    @Test
    void signupTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/member")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                        objectMapper.writeValueAsString(
                                MemberDto.builder()
                                .email("signuptest@mail.com")
                                .birthDate(LocalDate.of(1999,2,3))
                                .contact("01012341234")
                                .name("테스터")
                                .password("12345678a!")
                                .passwordConfirm("12345678a!")
                                .build()
                        )
                ))
                .andExpect(status().isOk());
    }

    @DisplayName("회원가입-eamil형식에러")
    @Test
    void signupValidationTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/member")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                        objectMapper.writeValueAsString(
                                MemberDto.builder()
                                        .email("signuptest")
                                        .birthDate(LocalDate.of(1999,2,3))
                                        .contact("01012341234")
                                        .name("테스터")
                                        .password("12345678a!")
                                        .build()
                        )
                ))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("회원가입-비밀번호형식에러")
    @Test
    void signupPasswordValidationTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/member")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                        objectMapper.writeValueAsString(
                                MemberDto.builder()
                                        .email("signuptest@mail.com")
                                        .birthDate(LocalDate.of(1999,2,3))
                                        .contact("01012341234")
                                        .name("테스터")
                                        .password("password123")
                                        .build()
                        )
                ))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("회원가입- 이름&연락처형식에러")
    @Test
    void signupNameContactValidationTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/member")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                        objectMapper.writeValueAsString(
                                MemberDto.builder()
                                        .email("signuptest@mail.com")
                                        .birthDate(LocalDate.of(1999,2,3))
                                        .contact("010547")
                                        .name("테스ter")
                                        .password("12345678a!")
                                        .build()
                        )
                ))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("회원정보수정-password")
    @Test
    void updateMemberPasswordTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/api/member")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                        objectMapper.writeValueAsString(
                                MemberDto.builder()
                                        .password("31245751@!@a")
                                        .build()
                        )
                ))
                .andExpect(status().isOk());
    }

    @DisplayName("회원정보수정-birthDate")
    @Test
    void updateMemberBirthDate() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/api/member")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                        objectMapper.writeValueAsString(
                                MemberDto.builder()
                                        .birthDate(LocalDate.of(1987,12,20))
                                        .build()
                        )
                ))
                .andExpect(jsonPath("$.birthDate").value("1987-12-20"))
                .andExpect(status().isOk());
    }

    @DisplayName("회원탈퇴")
    @Test
    void withdrawalTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/member")
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }


}