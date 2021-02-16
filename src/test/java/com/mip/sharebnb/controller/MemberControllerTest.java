package com.mip.sharebnb.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mip.sharebnb.dto.MemberDto;
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

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void before(WebApplicationContext wac) {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .addFilter(new CharacterEncodingFilter("UTF-8", true))
                .alwaysDo(print())
                .build();
    }

    @Test
    void getMemberTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/member/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test123@gmail.com"));
    }

    @Test
    void signupTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/member")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                        objectMapper.writeValueAsString(
                                MemberDto.builder()
                                .email("signuptest@mail.com")
                                .birthDate(LocalDate.now())
                                .contact("011-111-1234")
                                .name("tester")
                                .password("password")
                                .build()
                        )
                ))
                .andExpect(status().isOk());
    }

    @DisplayName("eamil형식에러")
    @Test
    void signupValidationTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/member")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                        objectMapper.writeValueAsString(
                                MemberDto.builder()
                                        .email("signuptest")
                                        .birthDate(LocalDate.of(2000,2,22))
                                        .contact("01111111234")
                                        .name("tester")
                                        .password("password1234")
                                        .build()
                        )
                ))
                .andExpect(status().isNotFound());
    }

    @DisplayName("비밀번호형식에러")
    @Test
    void signupPasswordValidationTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/member")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                        objectMapper.writeValueAsString(
                                MemberDto.builder()
                                        .email("signuptest@member.com")
                                        .birthDate(LocalDate.of(2000,2,22))
                                        .contact("01111111234")
                                        .name("tester")
                                        .password("password1234")
                                        .build()
                        )
                ))
                .andExpect(status().isNotFound());
    }

    @DisplayName("이름&연락처형식에러")
    @Test
    void signupNameContactValidationTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/member")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                        objectMapper.writeValueAsString(
                                MemberDto.builder()
                                        .email("signuptest@member.com")
                                        .birthDate(LocalDate.of(2000,2,22))
                                        .contact("0111eee")
                                        .name("테스ter")
                                        .password("password12#$")
                                        .build()
                        )
                ))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateMemberTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/member")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                        objectMapper.writeValueAsString(
                                MemberDto.builder()
                                        .email("updatetest@mail.com")
                                        .birthDate(LocalDate.now())
                                        .contact("011-111-1234")
                                        .name("tester")
                                        .password("password")
                                        .build()
                        )
                ));
        mockMvc.perform(MockMvcRequestBuilders.put("/api/member/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                        objectMapper.writeValueAsString(
                                MemberDto.builder()
                                        .email("updatetest2@mail.com")
                                        .build()
                        )
                ))
                .andExpect(status().isOk());
    }

    @Test
    void withdrawalTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/member")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                        objectMapper.writeValueAsString(
                                MemberDto.builder()
                                        .email("deletetest@mail.com")
                                        .birthDate(LocalDate.now())
                                        .contact("011-111-1234")
                                        .name("tester")
                                        .password("password")
                                        .build()
                        )
                ));
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/member/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }


}