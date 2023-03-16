package com.developlife.reviewtwits.utils.oauth;

import com.developlife.reviewtwits.exception.user.TokenInvalidException;
import com.developlife.reviewtwits.message.response.oauth.GoogleUserInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;

/**
 * @author ghdic
 * @since 2023/03/14
 */
public class GoogleOAuth2Utils {

    private static final String USER_INFO_ENDPOINT = "https://www.googleapis.com/oauth2/v3/userinfo";

    public static GoogleUserInfo getUserInfo(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();

        // Set the access token in the Authorization header
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

        RequestEntity<Void> request = RequestEntity
            .get(URI.create(USER_INFO_ENDPOINT))
            .headers(headers)
            .build();

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                request,
                String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.readValue(response.getBody(), GoogleUserInfo.class);
            }
        } catch (IOException | HttpClientErrorException e) {

        }
        throw new TokenInvalidException("토큰이 유효하지 않습니다");
    }
}
