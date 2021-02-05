package com.mip.sharebnb.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mip.sharebnb.dto.MemberDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = "spring.config.location="
        + "classpath:application.yml,"
        + "classpath:datasource.yml")
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
        mockMvc.perform(MockMvcRequestBuilders.post("/api/member")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                        objectMapper.writeValueAsString(
                                MemberDto.builder()
                                        .email("getmembertest@mail.com")
                                        .birthDate(LocalDate.now())
                                        .contact("011-111-1234")
                                        .name("tester")
                                        .password("password")
                                        .build()
                        )
                ));
        mockMvc.perform(MockMvcRequestBuilders.get("/api/member/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("getmembertest@mail.com"));
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