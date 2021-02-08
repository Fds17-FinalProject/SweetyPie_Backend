package com.mip.sharebnb;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class SharebnbApplication {

    public static final String APPLICATION_LOCATIONS = "spring.config.location="
            + "classpath:application.yml,"
            + "classpath:datasource.yml";
//            + "/home/ubuntu/conf/application.yml,"
//            + "/home/ubuntu/conf/datasource.yml";

    public static void main(String[] args) {
        new SpringApplicationBuilder(SharebnbApplication.class)
                .properties(APPLICATION_LOCATIONS)
                .run(args);
    }
}