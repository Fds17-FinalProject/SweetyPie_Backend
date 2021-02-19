package com.sweetypie.sweetypie.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest(properties = "spring.config.location="
        + "classpath:test.yml")
class BookmarkControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void before(WebApplicationContext was) {
        mockMvc = MockMvcBuilders.webAppContextSetup(was)
                .alwaysDo(print())
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .build();
    }

    @DisplayName("북마크 가져오기")
    @Test
    void getBookmarks() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/bookmark"))
                .andExpect(status().isBadRequest());
//                .andExpect(jsonPath("$", hasSize(3)));
    }

    @DisplayName("북마크 제거")
    @Test
    void deleteBookmark() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/bookmark/1"))
                .andExpect(status().isBadRequest());
    }
}