package com.mip.sharebnb;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "spring.config.location="
        + "classpath:application.yml,"
        + "classpath:datasource.yml")
class SharebnbApplicationTests {

    @Test
    void contextLoads() {
    }

}
