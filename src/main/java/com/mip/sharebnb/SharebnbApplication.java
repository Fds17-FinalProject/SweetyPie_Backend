package com.mip.sharebnb;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class SharebnbApplication {

    public static final String APPLICATION_LOCATIONS = "spring.config.location="
//            + "/home/ubuntu/conf/application.yml,"
//            + "/home/ubuntu/conf/datasource.yml";
            + "classpath:application.yml,"
            + "classpath:datasource.yml";

    public static void main(String[] args) {
        new SpringApplicationBuilder(SharebnbApplication.class)
                .properties(APPLICATION_LOCATIONS)
                .run(args);
    }
}