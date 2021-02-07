package com.mip.sharebnb.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mip.sharebnb.dto.LoginDto;
import com.mip.sharebnb.dto.TokenDto;
import com.mip.sharebnb.security.jwt.JwtFilter;
import com.mip.sharebnb.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@Valid @RequestBody LoginDto loginDto) {

        String jwt = authService.login(loginDto);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, JwtFilter.HEADER_PREFIX + jwt);

        return new ResponseEntity<>(new TokenDto(jwt), httpHeaders, HttpStatus.OK);
    }

    @GetMapping("login/google")
    public ResponseEntity<Map<String, String>> googleLogin(@RequestParam(value = "code") String authCode) throws JsonProcessingException {

        Map<String, String> userInfo = authService.getGoogleUserInfo(authCode);

        return new ResponseEntity<>(userInfo, HttpStatus.OK);
    }
}
