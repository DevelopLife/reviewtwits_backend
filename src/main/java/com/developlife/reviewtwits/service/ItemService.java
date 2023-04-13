package com.developlife.reviewtwits.service;

import com.developlife.reviewtwits.entity.ItemDetail;
import com.developlife.reviewtwits.entity.RelatedProduct;
import com.developlife.reviewtwits.entity.Review;
import com.developlife.reviewtwits.entity.User;
import com.developlife.reviewtwits.exception.item.CrawlingFailedException;
import com.developlife.reviewtwits.repository.ItemDetailRepository;
import com.developlife.reviewtwits.repository.RelatedProductRepository;
import com.developlife.reviewtwits.repository.ReviewRepository;
import com.developlife.reviewtwits.repository.UserRepository;
import com.developlife.reviewtwits.type.FileReferenceType;
import com.developlife.reviewtwits.type.MadeMultipartFile;
import com.developlife.reviewtwits.utils.oauth.selenium.CrawlingCoupang;
import com.github.javafaker.Faker;
import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author ghdic
 * @since 2023/03/22
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ItemService {

    private final RelatedProductRepository relatedProductRepository;
    private final ItemDetailRepository itemDetailRepository;
    private final CrawlingCoupang crawlingCoupang;

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
        return response;
    }

    @Transactional
    public RelatedProduct requestCrawlingProductInfo(String productName) {

        Optional<ItemDetail> searchAlreadyRegisterProduct = itemDetailRepository.findByProductNameLike(productName);
        if(searchAlreadyRegisterProduct.isPresent()){
            return searchAlreadyRegisterProduct.get().getRelatedProduct();
        }

        WebDriver driver = crawlingCoupang.getDriverWithOptions();

        try{
            RelatedProduct firstRelatedProduct = crawlingCoupang.saveRelatedFiveProductAndGetFirstProduct(productName, driver);

            // 크롤링 실패
            if(firstRelatedProduct == null){
                throw new CrawlingFailedException("키워드와 관련된 상품을 찾지못해서 크롤링에 실패했습니다.");
            }

            relatedProductRepository.save(firstRelatedProduct);
            // 나머지 크롤링 작업은 스레드로 넘겨주고 결과값을 리턴
            crawlingCoupang.crawlingItemDetailInfo(firstRelatedProduct, productName);

            return firstRelatedProduct;

        }finally{
            crawlingCoupang.closeDriver(driver);
        }
    }






}
