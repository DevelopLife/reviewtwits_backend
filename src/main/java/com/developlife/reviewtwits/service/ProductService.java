package com.developlife.reviewtwits.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.net.http.HttpHeaders;
import java.util.List;

/**
 * @author ghdic
 * @since 2023/03/22
 */
@Service
public class ProductService {
    public String search(String productName) {
        if(productName == null) {
            return "";
        }
        WebClient webClient = WebClient.builder()
            .baseUrl("https://www.coupang.com")
            .build();
        String response = webClient.get()
            .uri("/np/search/autoComplete?callback=&keyword=" + productName)
            .retrieve()
            .bodyToMono(String.class)
            .block();
        System.out.println(response);
        return response;
    }

    public void relateProductsCrawling(String productName) {
    }
}
