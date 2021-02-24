package com.sweetypie.sweetypie.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sweetypie.sweetypie.dto.BookmarkDto;
import com.sweetypie.sweetypie.dto.LoginDto;
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

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest(properties = "spring.config.location="
        + "classpath:test.yml")
class BookmarkControllerTest {

    @Autowired
    AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    private String token;

    @BeforeEach
    void before(WebApplicationContext was) {
        mockMvc = MockMvcBuilders.webAppContextSetup(was)
                .alwaysDo(print())
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .build();

        LoginDto loginDto = new LoginDto();
        loginDto.setEmail("test123@gmail.com");
        loginDto.setPassword("12345678a!");

        token = authService.login(loginDto);
    }

    @DisplayName("북마크 가져오기")
    @Test
    void getBookmarks() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/bookmark")
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @DisplayName("북마크 추가")
    @Test
    void postBookmark() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/bookmark")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(new BookmarkDto(3L))))
                .andExpect(status().isOk());
    }

    @DisplayName("북마크 추가 (중복)")
    @Test
    void postBookmarkException1() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/bookmark")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(new BookmarkDto(1L))))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("북마크 추가 (없는 숙소)")
    @Test
    void postBookmarkException2() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/bookmark")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(new BookmarkDto(100L))))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("북마크 제거")
    @Test
    void deleteBookmark() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/bookmark/1")
                .header("Authorization", token))
                .andExpect(status().isOk());
    }
}