package com.developlife.reviewtwits.utils.oauth;

/**
 * @author ghdic
 * @since 2023/03/14
 */
import com.developlife.reviewtwits.exception.user.TokenInvalidException;
import com.developlife.reviewtwits.message.response.oauth.KakaoUserInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Collections;

public class KakaoOauth2Utils {

    private static final String USER_INFO_ENDPOINT = "https://kapi.kakao.com/v1/oidc/userinfo";

    public static KakaoUserInfo getUserInfo(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();

        // Set the access token in the Authorization header
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // Build the userinfo request
        HttpEntity<String> request = new HttpEntity<>(headers);
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                USER_INFO_ENDPOINT,
                HttpMethod.GET,
                request,
                String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.readValue(response.getBody(), KakaoUserInfo.class);
            }
        } catch (IOException | HttpClientErrorException e) {
            e.printStackTrace();
        }

        throw new TokenInvalidException("토큰이 유효하지 않습니다");
    }
}