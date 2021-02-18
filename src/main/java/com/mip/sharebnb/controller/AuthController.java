package com.mip.sharebnb.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mip.sharebnb.dto.*;
import com.mip.sharebnb.security.jwt.JwtFilter;
import com.mip.sharebnb.service.AuthService;
import com.mip.sharebnb.service.BookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final BookmarkService bookmarkService;

    @PostMapping("/login")
    public ResponseEntity<List<BookmarkDto>> login(@Valid @RequestBody LoginDto loginDto) {

        String jwt = authService.login(loginDto);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, JwtFilter.HEADER_PREFIX + jwt);

        List<BookmarkDto> bookmarks = bookmarkService.findBookmarksByMemberEmail(loginDto.getEmail());

        return new ResponseEntity<>(bookmarks, httpHeaders, HttpStatus.OK);
    }

    @GetMapping("/login/google")
    public ResponseEntity<Map<String, String>> googleLogin(@RequestParam(value = "code") String authCode) throws JsonProcessingException {

        Map<String, String> map;
            map = authService.googleLogin(authCode);
        // 로그인 되면 200과 token 리턴
        if (map.get("token")!=null) {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, JwtFilter.HEADER_PREFIX + map.get("token"));
            return new ResponseEntity<>(map, httpHeaders,  HttpStatus.OK);
        // 로그인 안되면 203과 memberDto 리턴
        } else {
            return new ResponseEntity<>(map, HttpStatus.NON_AUTHORITATIVE_INFORMATION);
        }
    }

    @PostMapping ("/login/google")
    public ResponseEntity<TokenDto> signupBeforeGoogleLogin(@Valid @RequestBody GoogleMemberDto memberDto) {

        String token = authService.signupBeforeGoogleLogin(memberDto);

        return ResponseEntity.ok(new TokenDto(token));
    }

    @GetMapping("/logout")
    @PreAuthorize("authenticated")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        authService.logout(request);

        return new ResponseEntity<>("로그아웃 되었습니다", HttpStatus.OK);
    }
}
