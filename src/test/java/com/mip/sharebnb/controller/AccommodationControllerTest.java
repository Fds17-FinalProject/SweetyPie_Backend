package com.mip.sharebnb.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "spring.config.location="
        + "classpath:application.yml,"
        + "classpath:datasource.yml")
class AccommodationControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void before(WebApplicationContext was) {
        mockMvc = MockMvcBuilders.webAppContextSetup(was)
                .alwaysDo(print())
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .build();
    }

    @Test
    void getAccommodation() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/accommodation/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.city").value("서울특별시"))
                .andExpect(jsonPath("$.gu").value("마포구"))
                .andExpect(jsonPath("$.contact").value("010-1234-5678"));
    }

    @Test
    void getAllAccommodations() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/accommodations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(200)));
    }

    @Test
    void getAccommodationsByCity() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/accommodations/city/서울?page=0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].city").value("서울특별시"));
    }

    @Test
    void getAccommodationsBySearchKeyword() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/accommodations/search/서울?page=0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].city").value("서울특별시"));
    }

    @Test
    void getAccommodationsByCheckIn() {
    }
}