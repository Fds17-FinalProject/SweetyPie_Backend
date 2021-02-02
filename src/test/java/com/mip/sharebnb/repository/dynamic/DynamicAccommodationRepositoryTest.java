package com.mip.sharebnb.repository.dynamic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
class DynamicAccommodationRepositoryTest {

    private MockMvc mockMvc;

    @BeforeEach
    void before(WebApplicationContext was) {
        mockMvc = MockMvcBuilders.webAppContextSetup(was)
                .alwaysDo(print())
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .build();
    }

    @DisplayName("메인 검색 테스트")
    @Test
    void search() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/accommodations/search?searchKeyword=마포&checkIn=2021-02-05&checkout=2021-02-07&page=0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(10)))
                .andExpect(jsonPath("$[0].city").value("서울특별시"))
                .andExpect(jsonPath("$[0].gu").value("마포구"))
                .andExpect(jsonPath("$[0].contact").value("010-1234-5678"));

    }

    @DisplayName("검색어 없이 검색 테스트")
    @Test
    void searchIfThrown() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/accommodations/search?checkIn=2021-02-05&checkout=2021-02-07&page=0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(10)))
                .andExpect(jsonPath("$[0].city").value("서울특별시"))
                .andExpect(jsonPath("$[0].gu").value("마포구"))
                .andExpect(jsonPath("$[0].contact").value("010-1234-5678"));

    }
}