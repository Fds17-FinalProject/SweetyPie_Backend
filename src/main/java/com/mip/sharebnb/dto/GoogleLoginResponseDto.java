package com.mip.sharebnb.dto;

import lombok.Data;

@Data
public class GoogleLoginResponseDto {

    private String accessToken;

    private String expiresIn;

    private String refreshToken;

    private String scope;

    private String tokenType;

    private String idToken;
}
