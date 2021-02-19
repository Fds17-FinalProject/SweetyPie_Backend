package com.sweetypie.sweetypie.dto;

import lombok.Data;

@Data
public class GoogleTokenResponseDto {

    private String accessToken;

    private String expiresIn;

    private String refreshToken;

    private String scope;

    private String tokenType;

    private String idToken;
}
