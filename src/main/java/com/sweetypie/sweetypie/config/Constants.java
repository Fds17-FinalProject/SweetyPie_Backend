package com.sweetypie.sweetypie.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class Constants {

    private @Value("${jwt.secret}") String jwtSecret;
    private @Value("${jwt.token-validity-in-seconds}") long jwtTokenValidityInSeconds;
}
