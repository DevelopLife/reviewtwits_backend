package com.developlife.reviewtwits.service;

import com.developlife.reviewtwits.entity.ItemDetail;
import com.developlife.reviewtwits.entity.RelatedProduct;
import com.developlife.reviewtwits.exception.file.FileNotStoredException;
import com.developlife.reviewtwits.repository.ItemDetailRepository;
import com.developlife.reviewtwits.repository.RelatedProductRepository;
import com.developlife.reviewtwits.type.MadeMultipartFile;
import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;


import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

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

    private final FileStoreService fileStoreService;

    private static final String site = "https://www.coupang.com";
    private static final String searchUrl = "https://www.coupang.com/np/search?q=";
    private static final String ourImageRequestUrl = "https://reviewtwits.mcv.kr/request-images/";

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

    public void relateProductsCrawling(String productName) throws IOException {

        if(relatedProductRepository.existsByNameLike(productName)){
            return;
        }
        RelatedProduct firstRelatedProduct = saveRelatedFiveProductAndGetFirstProduct(productName);

        if(firstRelatedProduct == null){
            return;
        }

        Element targetDetailElement = getTargetDetailElementFromSelenium(firstRelatedProduct);

        ItemDetail detail = ItemDetail.builder()
                .relatedProduct(firstRelatedProduct)
                .detailInfo(targetDetailElement.html())
                .build();

        itemDetailRepository.save(detail);

        Elements imgElements = targetDetailElement.getElementsByTag("img");
        List<String> fileSourceList = getImageURLFromHTML(imgElements);
        storeDetailInfoImages(firstRelatedProduct, detail.getItemId(), fileSourceList);
        changeImageInfoInHtmlAndSave(targetDetailElement, detail, imgElements);
    }

    private RelatedProduct saveRelatedFiveProductAndGetFirstProduct(String productName) throws IOException {
        RelatedProduct firstRelatedProduct = null;

        StringJoiner name = getNameForURL(productName);
        Document document = coupangCrawlingConnection(searchUrl + name);
        List<Element> targetElements = collectTopFiveElements(document);

        List<RelatedProduct> crawlerList = new ArrayList<>();
        for(int elementIndex = 0; elementIndex < targetElements.size(); elementIndex++){
            RelatedProduct product = makeCrawlingProductInfo(targetElements.get(elementIndex));
            crawlerList.add(product);
            if(elementIndex == 0){
                firstRelatedProduct = product;
            }
        }

        relatedProductRepository.saveAll(crawlerList);

        for(RelatedProduct crawler : crawlerList){
            MultipartFile multipartFile = getImageFileFromUrl(crawler.getImagePath(),crawler.getName());
            fileStoreService.storeFiles(List.of(multipartFile),crawler.getProductId(),"ProductCrawler");
        }

        return firstRelatedProduct;
    }

    private Element getTargetDetailElementFromSelenium(RelatedProduct firstRelatedProduct) {
        WebDriver driver = getDriverWithOptions();
        try{
            driver.get(firstRelatedProduct.getProductUrl());

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#btfTab > ul.tab-contents > li.product-detail.tab-contents__content > div:nth-child(2)")));

            WebElement webElement = driver.findElement(By.cssSelector("#btfTab > ul.tab-contents > li.product-detail.tab-contents__content > div:nth-child(2)"));
            return Jsoup.parse(webElement.getAttribute("innerHTML")).body();
        }catch(Exception e){
            throw new FileNotStoredException("파일이 저장되고 있지 않습니다");
        }
        finally{
            driver.close();
        }
    }

    private List<String> getImageURLFromHTML(Elements imgElements) {
        List<String> fileSourceList = new ArrayList<>();
        for(Element element : imgElements){
            fileSourceList.add(element.attributes().get("src"));
        }
        return fileSourceList;
    }

    private void storeDetailInfoImages(RelatedProduct firstRelatedProduct, long itemDetailId, List<String> fileSourceList) {
        List<MultipartFile> multipartFileList = new ArrayList<>();
        for(int i = 0; i < fileSourceList.size(); i++){
            String fileName = firstRelatedProduct.getName() + "_" + i;
            MultipartFile multipartFile = getImageFileFromUrl("https:" + fileSourceList.get(i),fileName);
            multipartFileList.add(multipartFile);
        }
        fileStoreService.storeFiles(multipartFileList, itemDetailId,"ItemDetail");
    }

    private void changeImageInfoInHtmlAndSave(Element targetDetailElement, ItemDetail detail, Elements imgElements) {
        List<String> itemImageNameList = fileStoreService.bringFileNameList("ItemDetail", detail.getItemId());

        for(int i = 0; i < imgElements.size(); i++){
            imgElements.get(i).attr("src",ourImageRequestUrl + itemImageNameList.get(i));
        }

        detail.setDetailInfo(targetDetailElement.html());
        itemDetailRepository.save(detail);
    }

    private WebDriver getDriverWithOptions() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("headless");
        options.addArguments("user-agent=Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.104 Whale/3.13.131.36 Safari/537.36");

        return new ChromeDriver(options);
    }

    private List<Element> collectTopFiveElements(Document document){
        List<Element> targetElements = new ArrayList<>();
        for (Element element : document.getElementsByClass("search-product-wrap")) {
            Element siblingElement = element.nextElementSibling();
            if (siblingElement.hasClass("number") && Integer.parseInt(siblingElement.text()) <= 5) {
                targetElements.add(element);
            }
        }
        return targetElements;
    }

    private RelatedProduct makeCrawlingProductInfo(Element element) {
        Element parentElement = element.parent();
        String productURL = site + parentElement.attributes().get("href");

        Element imgElement = element.child(0).child(0);
        String imagePath = "https:" + imgElement.attributes().get("src");

        Element description = element.child(1).child(0);

        String registeredName = description.getElementsByClass("name").first().text();
        int price = getPriceFromText(description.getElementsByClass("price-value").first().text());

        return RelatedProduct.builder()
                                .name(registeredName)
                                .price(price)
                                .imagePath(imagePath)
                                .productUrl(productURL)
                                .build();
    }

    private Document coupangCrawlingConnection(String url) throws IOException {
        return Jsoup.connect(url)
                .maxBodySize(0)
                .header("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) " +
                        "Chrome/111.0.0.0 Safari/537.36")
                .header("cookie", "PCID=31489593180081104183684; _fbp=fb.1.1644931520418.1544640325; gd1=Y; X-CP-PT-locale=ko_KR; MARKETID=31489593180081104183684; sid=03ae1c0ed61946c19e760cf1a3d9317d808aca8b; x-coupang-origin-region=KOREA; x-coupang-target-market=KR; x-coupang-accept-language=ko_KR;")
                .timeout(5000)
                .get();
    }

    private StringJoiner getNameForURL(String productName) {
        String[] productNameArray = productName.split(" ");
        StringJoiner joiner = new StringJoiner("+");
        for(String name : productNameArray){
            joiner.add(name);
        }
        return joiner;
    }
    private int getPriceFromText(String text){
        return Integer.parseInt(text.replaceAll(",",""));
    }

    private MultipartFile getImageFileFromUrl(String uri, String productName) {
        WebClient webClient = WebClient.builder().
                exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer.defaultCodecs().
                                maxInMemorySize(10 * 1024 * 1024)).build()).build();

        byte[] imageBytes = webClient.get()
                .uri(uri)
                .accept(MediaType.IMAGE_JPEG)
                .retrieve()
                .bodyToMono(byte[].class).block();

        String fileName = insertUnderBarInName(productName) + ".jpg";
        return new MadeMultipartFile(imageBytes,fileName);
    }
    private String insertUnderBarInName(String name){
        StringJoiner joiner = new StringJoiner("_");
        String[] nameArray = name.split(" ");
        for(String part : nameArray){
            joiner.add(part);
        }
        return joiner.toString();
    }
}
