package com.developlife.reviewtwits.service;

import com.developlife.reviewtwits.entity.RelatedProduct;
import com.developlife.reviewtwits.repository.RelatedProductRepository;
import com.developlife.reviewtwits.type.MadeMultipartFile;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

/**
 * @author ghdic
 * @since 2023/03/22
 */
@Service
@RequiredArgsConstructor
public class itemService {

    private final RelatedProductRepository relatedProductRepository;
    private final FileStoreService fileStoreService;

    private static final String site = "https://www.coupang.com";

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

        StringJoiner name = getNameForURL(productName);
        Document document = coupangCrawlingConnection(name);
        List<Element> targetElements = collectTopFiveElements(document);

        List<RelatedProduct> crawlerList = new ArrayList<>();
        for(Element element : targetElements){
            RelatedProduct product = makeCrawlingProductInfo(element);
            crawlerList.add(product);
        }

        relatedProductRepository.saveAll(crawlerList);

        for(RelatedProduct crawler : crawlerList){
            MultipartFile multipartFile = getImageFileFromUrl(crawler.getImagePath(),crawler.getName());
            fileStoreService.storeFiles(List.of(multipartFile),crawler.getProductId(),"ProductCrawler");
        }
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

    private Document coupangCrawlingConnection(StringJoiner name) throws IOException {
        return Jsoup.connect("https://www.coupang.com/np/search?q="
                        + name)
                .header("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) " +
                        "Chrome/111.0.0.0 Safari/537.36")
                .header("cookie", "PCID=31489593180081104183684; _fbp=fb.1.1644931520418.1544640325; gd1=Y; X-CP-PT-locale=ko_KR; MARKETID=31489593180081104183684; sid=03ae1c0ed61946c19e760cf1a3d9317d808aca8b; x-coupang-origin-region=KOREA; x-coupang-target-market=KR; x-coupang-accept-language=ko_KR;")
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
        WebClient webClient = WebClient.builder().build();

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
