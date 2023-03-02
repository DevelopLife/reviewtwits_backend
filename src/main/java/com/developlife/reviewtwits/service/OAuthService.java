package com.developlife.reviewtwits.service;

import com.developlife.reviewtwits.exception.user.OAuthRequestInvalidException;
import com.developlife.reviewtwits.message.request.user.OAuthTokenRequest;
import com.developlife.reviewtwits.message.response.user.JwtTokenResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author ghdic
 * @since 2023/03/02
 */
@Service
@PropertySource("classpath:oauth.properties")
public class OAuthService {

    @Value("${kakao.client_id}")
    private String kakaoClientId;

    private String requestKakaoJwtToken(OAuthTokenRequest reqeust) throws IOException, URISyntaxException, InterruptedException {
        URI uri = new URI("https://kauth.kakao.com/oauth/token");
        Map<String, String> parameters = new HashMap<>();
        parameters.put("code", reqeust.code());
        parameters.put("grant_type", "authorization_code");
        parameters.put("client_id", kakaoClientId);
        parameters.put("redirect_uri", "");


        String form = parameters.entrySet()
                .stream()
                .map(e -> e.getKey() + "=" + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
                .collect(Collectors.joining("&"));

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://kauth.kakao.com/oauth/token"))
                .headers("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(form))
                .build();

        HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if(response.statusCode() != 200) {
            throw new OAuthRequestInvalidException("Kakao OAuth Token Request Failed");
        }

        return "";
    }
}
