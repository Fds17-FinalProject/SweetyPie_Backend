package com.sweetypie.sweetypie;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "spring.config.location="
        + "classpath:test.yml")
class SweetyPieApplicationTests {

    @Test
    void contextLoads() {
    }

}
