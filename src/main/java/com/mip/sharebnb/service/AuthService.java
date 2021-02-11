package com.mip.sharebnb.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.mip.sharebnb.dto.*;
import com.mip.sharebnb.exception.DuplicateValueExeption;
import com.mip.sharebnb.model.Member;
import com.mip.sharebnb.repository.MemberRepository;
import com.mip.sharebnb.security.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final MemberRepository memberRepository;
    private final MemberService memberService;
    private final RedisTemplate<String, String> redisTemplate;

    final static String GOOGLE_TOKEN_BASE_URL = "https://oauth2.googleapis.com/token";
    final static String GOOGLE_REDIRECT_URL = "http://localhost:3000/redirect";
    final static String GOOGLE_REVOKE_TOKEN_BASE_URL = "https://oauth2.googleapis.com/revoke";

    @Value("${jwt.token-validity-in-seconds}") long tokenValidityInSeconds;

    @Value("${google.client_id}")
    String clientId;
    @Value("${google.client_secret}")
    String clientSecret;

    public String login(LoginDto loginDto) {

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return tokenProvider.createToken(authentication);
    }

    public Map<String, String> googleLogin(String authCode) throws JsonProcessingException {

        Map<String, String> googleUserInfo = getGoogleUserInfo(authCode);

        GoogleMemberDto memberDto = pareUserInfoToGoogleMemberDto(googleUserInfo);

        // 로그인이 가능할 때 로그인을 해서 JWT 토큰을 리턴한다
        if (isLoginPossible(memberDto, googleUserInfo)) {
            LoginDto loginDto = new LoginDto(memberDto.getEmail(), memberDto.getSocialId());
            Map<String, String> map = new HashMap<>();
            map.put("token", login(loginDto));

            return map;
        // 로그인이 불가능 할 때 회원가입 가능하게 memberDto 를 리턴한다
        } else {
            Map<String, String> map = new HashMap<>();
            map.put("email", memberDto.getEmail());
            map.put("name", memberDto.getName());
            map.put("socialId", memberDto.getSocialId());

            return map;
        }

    }

    public String signupBeforeGoogleLogin(GoogleMemberDto memberDto) {

        memberService.signupGoogleMember(memberDto);
        LoginDto loginDto = new LoginDto(memberDto.getEmail(), memberDto.getSocialId());
        return login(loginDto);

    }

    public void logout(String token) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(token, "", Duration.ofSeconds(tokenValidityInSeconds));
    }

    private Map<String, String> getGoogleUserInfo(String authCode) throws JsonProcessingException {

        //HTTP Request를 위한 RestTemplate
        RestTemplate restTemplate = new RestTemplate();

        //Google OAuth Access Token 요청을 위한 파라미터 세팅
        GoogleTokenRequestDto googleOAuthRequestParam = GoogleTokenRequestDto
                .builder()
                .clientId(clientId)
                .clientSecret(clientSecret)
                .code(authCode)
                .redirectUri(GOOGLE_REDIRECT_URL)
                .grantType("authorization_code").build();
        //JSON 파싱을 위한 기본값 세팅
        //요청시 파라미터는 스네이크 케이스로 세팅되므로 Object mapper에 미리 설정해준다.
        ObjectMapper mapper = new ObjectMapper();
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        //AccessToken 발급 요청
        ResponseEntity<String> resultEntity = restTemplate.postForEntity(GOOGLE_TOKEN_BASE_URL, googleOAuthRequestParam, String.class);

        //Token Request
        GoogleTokenResponseDto result = mapper.readValue(resultEntity.getBody(), new TypeReference<GoogleTokenResponseDto>() {
        });

        System.out.println(resultEntity.getBody());

        //ID Token만 추출 (사용자의 정보는 jwt로 인코딩 되어있다)
        String jwtToken = result.getIdToken();
        String requestUrl = UriComponentsBuilder.fromHttpUrl("https://oauth2.googleapis.com/tokeninfo")
                .queryParam("id_token", jwtToken).encode().toUriString();

        String resultJson = restTemplate.getForObject(requestUrl, String.class);

        Map<String, String> userInfo = mapper.readValue(resultJson, new TypeReference<Map<String, String>>(){});
        userInfo.put("accessToken", result.getAccessToken());
        return userInfo;
    }

    private GoogleMemberDto pareUserInfoToGoogleMemberDto (Map<String, String> userInfo) {

        return  GoogleMemberDto.builder()
                .email(userInfo.get("email"))
                .name(userInfo.get("name"))
                .socialId(userInfo.get("sub"))
                .build();
    }

    private boolean isLoginPossible(GoogleMemberDto memberDto, Map<String, String> userInfo) {
        Optional<Member> optionalMember = memberRepository.findByEmail(memberDto.getEmail());

        if (!optionalMember.isPresent()) {
            return false;
        } else {
            Member member = optionalMember.get();
            if (member.isSocialMember()) {
                return true;
            // google 메일이 이미 가입되어 있을때 에러를 내보낸다
            } else {
                // 액세스 토큰이 필요없으니 만료시킨다
                revokeAccessToken(userInfo.get("accessToken"));
                throw new DuplicateValueExeption("이미 가입된 회원입니다 => 액세스토큰을 만료 시켰습니다");
            }
        }
    }

    private void revokeAccessToken(String token) {

        RestTemplate restTemplate = new RestTemplate();
        final String requestUrl = UriComponentsBuilder.fromHttpUrl(GOOGLE_REVOKE_TOKEN_BASE_URL)
                .queryParam("token", token).encode().toUriString();

        String resultJson = restTemplate.postForObject(requestUrl, null, String.class);
    }

}
