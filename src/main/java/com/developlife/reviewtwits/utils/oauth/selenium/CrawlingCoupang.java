package com.developlife.reviewtwits.utils.oauth.selenium;

import com.developlife.reviewtwits.entity.ItemDetail;
import com.developlife.reviewtwits.entity.RelatedProduct;
import com.developlife.reviewtwits.entity.Review;
import com.developlife.reviewtwits.entity.User;
import com.developlife.reviewtwits.repository.ItemDetailRepository;
import com.developlife.reviewtwits.repository.RelatedProductRepository;
import com.developlife.reviewtwits.repository.review.ReviewRepository;
import com.developlife.reviewtwits.repository.UserRepository;
import com.developlife.reviewtwits.service.FileStoreService;
import com.developlife.reviewtwits.type.ReferenceType;
import com.developlife.reviewtwits.type.MadeMultipartFile;
import com.github.javafaker.Faker;
import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author ghdic
 * @since 2023/04/13
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CrawlingCoupang {

    private final RelatedProductRepository relatedProductRepository;
    private final ItemDetailRepository itemDetailRepository;
    private final UserRepository userRepository;
    private final FileStoreService fileStoreService;
    private final ReviewRepository reviewRepository;
    private Faker faker = new Faker();

    private static final String site = "https://www.coupang.com";
    private static final String searchUrl = "https://www.coupang.com/np/search?q=";

    public WebDriver getDriverWithOptions() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--window-size=1920,1080");
        // options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("-disable-gpu");
        options.addArguments("user-agent=Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.104 Whale/3.13.131.36 Safari/537.36");
        options.addArguments("--disable-blink-features=AutomationControlled");
        ChromeDriver driver = new ChromeDriver(options);
        return driver;
    }

    @Transactional
    public void createReviewFromProduct(String productUrl, String productName, int targetScore, int targetCount, WebDriver driver) throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        JavascriptExecutor js = (JavascriptExecutor) driver;
        driver.get(productUrl);
        // 상품평 클릭
        // scrollDownWhileLastHeight(js);
        // 로드 후 바로 클릭할 경우 제대로 탭 이동이 안되는 현상이 있음 1초 대기
        Thread.sleep(1000);
        driver.findElement(new By.ByCssSelector("#btfTab > ul.tab-titles > li:nth-child(2)")).click();
        // 모든 별점 보기 박스 선택
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#btfTab > ul.tab-contents > li.product-review.tab-contents__content > div > div.sdp-review__article.js_reviewArticleContainer > section.sdp-review__article__order.js_reviewArticleOrderContainer.sdp-review__article__order--active > div.sdp-review__article__order__star.js_reviewArticleSearchStarSelectBtn")));
        driver.findElement(new By.ByCssSelector("#btfTab > ul.tab-contents > li.product-review.tab-contents__content > div > div.sdp-review__article.js_reviewArticleContainer > section.sdp-review__article__order.js_reviewArticleOrderContainer.sdp-review__article__order--active > div.sdp-review__article__order__star.js_reviewArticleSearchStarSelectBtn")).click();
        // 별점 선택
        driver.findElement(new By.ByCssSelector(String.format("#btfTab > ul.tab-contents > li.product-review.tab-contents__content > div > div.sdp-review__article.js_reviewArticleContainer > section.sdp-review__article__order.js_reviewArticleOrderContainer.sdp-review__article__order--active > div.sdp-review__article__order__star.js_reviewArticleSearchStarSelectBtn > div.sdp-review__article__order__star__option.js_reviewArticleStarSelectOptionContainer > ul > li:nth-child(%d)", 6 - targetScore))).click();
        // 해당 별점의 리뷰가 보일때까지 최대 5초 대기
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(String.format("#btfTab > ul.tab-contents > li.product-review.tab-contents__content > div > div.sdp-review__article.js_reviewArticleContainer > section.js_reviewArticleListContainer > article > div.sdp-review__article__list__info > div.sdp-review__article__list__info__product-info > div.sdp-review__article__list__info__product-info__star-gray > div[data-rating=\"%d\"]", targetScore))));
        // artcle 태그에 묶여있는 리뷰들을 가져옴기
        Element innerHTML = Jsoup.parse(driver.findElement(new By.ByCssSelector("#btfTab > ul.tab-contents > li.product-review.tab-contents__content > div > div.sdp-review__article.js_reviewArticleContainer > section.js_reviewArticleListContainer")).getAttribute("innerHTML")).body();
        Elements articles = innerHTML.select("article");
        int reviewCount = 0;
        for (Element review : articles) {
            if(++reviewCount > targetCount) {
                break;
            }

            String profileImageUrl = review.selectFirst("div.sdp-review__article__list__info > div.sdp-review__article__list__info__profile > img").attr("src");
            String nickname = review.selectFirst("div.sdp-review__article__list__info > div.sdp-review__article__list__info__user > span").text();
            String score = review.selectFirst("div.sdp-review__article__list__info > div.sdp-review__article__list__info__product-info > div.sdp-review__article__list__info__product-info__star-gray > div").attr("data-rating");
            String date = review.selectFirst("div.sdp-review__article__list__info > div.sdp-review__article__list__info__product-info > div.sdp-review__article__list__info__product-info__reg-date").text();
            Elements images = review.select("div.sdp-review__article__list__attachment.js_reviewArticleListGalleryContainer > div > img");
            List<String> imageUrls = new ArrayList<>();

            for (Element image : images) {
                imageUrls.add(image.attr("src"));
            }
            String content;
            try {
                content = review.selectFirst("div.js_reviewArticleContentContainer > div.js_reviewArticleContent").text();
            } catch (NoSuchElementException e) {
                content = "";
            }
            Optional<User> userOptional = userRepository.findByNickname(nickname);
            User user;
            if(userOptional.isPresent()) {
                user = userOptional.get();
            } else {
                String phoneNumber;
                do {
                    phoneNumber = faker.phoneNumber().phoneNumber();
                } while (userRepository.existsByPhoneNumber(phoneNumber));
                user = User.builder()
                        .nickname(nickname)
                        .accountId(nickname + "@temp.com")
                        .phoneNumber(faker.phoneNumber().phoneNumber())
                        .build();
                userRepository.save(user);
                MultipartFile profileImageFile = getImageFileFromUrl( "https:" + profileImageUrl, user.getNickname());
                fileStoreService.storeFiles(List.of(profileImageFile), user.getUserId(), ReferenceType.USER);
            }
            Review registeredReview = Review.builder()
                    .content(content)
                    .user(user)
                    .score(Integer.parseInt(score))
                    .productName(productName)
                    .productUrl(productUrl)
                    .build();
            reviewRepository.save(registeredReview);
            registeredReview.setCreatedDate(LocalDateTime.parse(date + " 00:00:00", DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss")));
            List<MultipartFile> imageFiles = new ArrayList<>();
            for(String imageUrl : imageUrls) {
                imageFiles.add(getImageFileFromUrl(imageUrl, String.format("%s_%s_%d", user.getNickname(), productName, imageFiles.size())));
            }
            fileStoreService.storeFiles(imageFiles, registeredReview.getReviewId(), ReferenceType.REVIEW);
        }

    }

    private static void scrollDownWhileLastHeight(JavascriptExecutor js) {
        int SCROLL_PAUSE_TIME = 1000;
        Long lastHeight = (Long) js.executeScript("return document.body.scrollHeight");

        while (true) {
            js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
            try {
                Thread.sleep(SCROLL_PAUSE_TIME);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            Long newHeight = (Long) js.executeScript("return document.body.scrollHeight");
            if (newHeight.equals(lastHeight)) {
                break;
            }
            lastHeight = newHeight;
        }
    }

    @Transactional
    public RelatedProduct saveRelatedFiveProductAndGetFirstProduct(String productName, WebDriver chromeWebDriver){
        RelatedProduct firstRelatedProduct = null;

        StringJoiner name = getNameForURL(productName);
        chromeWebDriver.get(searchUrl + name);

        List<WebElement> webElements = chromeWebDriver.findElements(new By.ByClassName("search-product-link"));

        List<Element> extractedElements = new ArrayList<>();
        for(WebElement toExtractElements : webElements){
            extractedElements.add(Jsoup.parse(toExtractElements.getAttribute("outerHTML")).body().child(0));
        }

        List<Element> targetElements = new ArrayList<>();
        for (Element element : extractedElements){
            if(element.childrenSize() >= 2){
                Element siblingElement = element.child(1);
                if (siblingElement.hasClass("number") && Integer.parseInt(siblingElement.text()) <= 5) {
                    targetElements.add(element);
                }
            }
        }

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
            fileStoreService.storeFiles(List.of(multipartFile),crawler.getProductId(), ReferenceType.RELATED_PRODUCT);
        }

        return firstRelatedProduct;
    }


    private Element getTargetDetailElementFromSelenium(RelatedProduct firstRelatedProduct, WebDriver chromeWebDriver) {
        log.info("전체 body 를 들고 오는 시도 시작");
        chromeWebDriver.get(firstRelatedProduct.getProductUrl());

        WebDriverWait wait = new WebDriverWait(chromeWebDriver, Duration.ofSeconds(15));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#btfTab > ul.tab-contents > li.product-detail.tab-contents__content > div:nth-child(2)")));

        WebElement webElement = chromeWebDriver.findElement(By.cssSelector("#btfTab > ul.tab-contents > li.product-detail.tab-contents__content > div:nth-child(2)"));
        return Jsoup.parse(webElement.getAttribute("innerHTML")).body();
    }

    private List<String> getImageURLFromHTML(Elements imgElements) {
        List<String> fileSourceList = new ArrayList<>();
        for(Element element : imgElements){
            fileSourceList.add(element.attributes().get("src"));
        }
        return fileSourceList;
    }

    @Transactional
    public void storeDetailInfoImages(RelatedProduct firstRelatedProduct, long itemDetailId, List<String> fileSourceList) {
        List<MultipartFile> multipartFileList = new ArrayList<>();
        for(int i = 0; i < fileSourceList.size(); i++){
            String fileName = firstRelatedProduct.getName() + "_" + i;
            MultipartFile multipartFile = getImageFileFromUrl("https:" + fileSourceList.get(i),fileName);
            multipartFileList.add(multipartFile);
        }
        fileStoreService.storeFiles(multipartFileList, itemDetailId, ReferenceType.ITEM_DETAIL);
    }

    private boolean checkCompleteUrl(String url) {
        Pattern p = Pattern.compile("^(http|https)://.*$");
        Matcher m = p.matcher(url);
        return m.matches();
    }

    @Transactional
    public void changeImageInfoInHtmlAndSave(Element targetDetailElement, ItemDetail detail, Elements imgElements) {
        List<String> itemImageNameList = fileStoreService.bringFileNameList(ReferenceType.ITEM_DETAIL, detail.getItemId());

        for(int i = 0; i < imgElements.size(); i++){
            imgElements.get(i).attr("src","/" + itemImageNameList.get(i));
        }

        detail.setDetailInfo(targetDetailElement.html());
        itemDetailRepository.save(detail);
    }




    @Async("threadPoolTaskExecutor")
    public void crawlingItemDetailInfo(RelatedProduct firstRelatedProduct, String productName) {
        // 상품 정보이미지 저장
        WebDriver driver = getDriverWithOptions();
        try {
            Element targetDetailElement = getTargetDetailElementFromSelenium(firstRelatedProduct, driver);
            Elements imgElements = targetDetailElement.getElementsByTag("img");
            for (Element element : imgElements) {
                String url = element.attributes().get("src");
                if (checkCompleteUrl(url)) {
                    element.remove();
                }
            }

            // 상품정보
            ItemDetail detail = ItemDetail.builder()
                    .relatedProduct(firstRelatedProduct)
                    .detailInfo(targetDetailElement.html())
                    .productName(productName)
                    .build();

            itemDetailRepository.save(detail);

            Elements targetImages = targetDetailElement.getElementsByTag("img");
            List<String> fileSourceList = getImageURLFromHTML(targetImages);
            storeDetailInfoImages(firstRelatedProduct, detail.getItemId(), fileSourceList);
            changeImageInfoInHtmlAndSave(targetDetailElement, detail, targetImages);
            log.info("자료 취합 완료");

            // 리뷰 5개짜리 3개를 긁어옴
            createReviewFromProduct(firstRelatedProduct.getProductUrl(), productName, 5, 3, driver);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            closeDriver(driver);
        }
    }



    private RelatedProduct makeCrawlingProductInfo(Element element) {
        String productURL = site + element.attributes().get("href");

        Element imgElement = element.child(0).child(0).child(0);
        String imagePath = "https:" + imgElement.attributes().get("src");

        Element description = element.child(0).child(1).child(0);

        String registeredName = description.getElementsByClass("name").first().text();
        int price = getPriceFromText(description.getElementsByClass("price-value").first().text());

        return RelatedProduct.builder()
                .name(registeredName)
                .price(price)
                .imagePath(imagePath)
                .productUrl(productURL)
                .build();
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
        System.out.println(uri);
        WebClient webClient = WebClient.builder().
                exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer.defaultCodecs().
                                maxInMemorySize(10 * 1024 * 1024)).build()).build();

        byte[] imageBytes = webClient.get()
                .uri(uri)
                .accept(new MediaType[]{MediaType.IMAGE_JPEG, MediaType.IMAGE_PNG})
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

    public void closeDriver(WebDriver driver) {
        try {
            driver.close();
        } catch (NoSuchSessionException e) {
            log.info("driver already closed");
        }
    }
}
