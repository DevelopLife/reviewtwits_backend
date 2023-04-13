package com.developlife.reviewtwits.review;

import com.developlife.reviewtwits.CommonSteps;
import com.developlife.reviewtwits.entity.Comment;
import com.developlife.reviewtwits.entity.ItemDetail;
import com.developlife.reviewtwits.entity.RelatedProduct;
import com.developlife.reviewtwits.entity.Review;
import com.developlife.reviewtwits.message.request.review.SnsCommentWriteRequest;
import com.developlife.reviewtwits.repository.CommentRepository;
import com.developlife.reviewtwits.repository.ItemDetailRepository;
import com.developlife.reviewtwits.repository.RelatedProductRepository;
import com.developlife.reviewtwits.repository.ReviewRepository;
import io.restassured.RestAssured;
import io.restassured.builder.MultiPartSpecBuilder;
import io.restassured.config.RestAssuredConfig;
import io.restassured.specification.MultiPartSpecification;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;
import static io.restassured.config.EncoderConfig.encoderConfig;

/**
 * @author WhalesBob
 * @since 2023-03-31
 */
@Component
public class SnsReviewSteps {
    final static String productName = "리뷰제품제품";
    final static String productURL = "http://www.example.com/123";
    final static String rightReviewText = "맛있고 좋아요! 어쩌구저쩌구.... 그랬어요!";
    final static String wrongReviewText = "좋아요";
    final static int starScore = 4;
    final static String commentContent = "테스트 코멘트";
    final static String changeCommentContent = "테스트를 위한 수정 코멘트";
    final static String reactionContent = "GOOD";
    final static String newReactionContent = "SUNGLASSES";
    final ReviewRepository reviewRepository;
    final ItemDetailRepository itemDetailRepository;
    final RelatedProductRepository relatedProductRepository;
    final CommentRepository commentRepository;

    public SnsReviewSteps(ReviewRepository reviewRepository, ItemDetailRepository itemDetailRepository,
                          RelatedProductRepository relatedProductRepository, CommentRepository commentRepository) {
        this.reviewRepository = reviewRepository;
        this.itemDetailRepository = itemDetailRepository;
        this.relatedProductRepository = relatedProductRepository;
        this.commentRepository = commentRepository;
    }
    public static List<MultiPartSpecification> 리뷰_이미지_파일정보_생성() {
        try{
            String fileFullName = "image.png";
            File file = new File(System.getProperty("java.io.tmpdir"), fileFullName);
            BufferedImage image = new BufferedImage(200,200,BufferedImage.TYPE_INT_ARGB);
            ImageIO.write(image,"png",file);
            return createMultipartFileList(file);
        }catch (IOException e){
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static List<MultiPartSpecification> 리뷰_이미지아닌_파일정보_생성() throws IOException {
        String fileFullName = "text.txt";
        Path path = new File(System.getProperty("java.io.tmpdir"), fileFullName).toPath();

        String inputContent = "hello world";
        Files.write(path, inputContent.getBytes());

        return createMultipartFileList(path.toFile());
    }

    private static List<MultiPartSpecification> createMultipartFileList(File file) throws IOException {
        DiskFileItem fileItem = new DiskFileItem("file", "application/octet-stream", false, file.getName(), (int) file.length() , file.getParentFile());
        fileItem.getOutputStream().write(Files.readAllBytes(file.toPath()));

        List<MultipartFile> multipartFiles = List.of(new CommonsMultipartFile(fileItem));
        MultiPartSpecification[] specifications = new MultiPartSpecification[multipartFiles.size()];
        for(int i = 0; i < multipartFiles.size(); i++){
            MultipartFile multipartFile = multipartFiles.get(i);
            specifications[i] = new MultiPartSpecBuilder(multipartFile.getBytes())
                    .controlName("multipartImageFiles")
                    .fileName(multipartFile.getOriginalFilename())
                    .mimeType(multipartFile.getContentType())
                    .build();
        }
        return Arrays.asList(specifications);
    }

    public static SnsCommentWriteRequest 댓글_작성정보_생성(){
        return SnsCommentWriteRequest.builder()
                .parentId(0)
                .content(commentContent)
                .build();
    }

    public Long SNS_리뷰_작성(String token, String content){
        RestAssured.config = new RestAssuredConfig().encoderConfig(encoderConfig().defaultContentCharset("UTF-8"));
        RequestSpecification request = given()
            .contentType("multipart/form-data")
            .header("X-AUTH-TOKEN", token)
            .multiPart("productURL", productURL)
            .multiPart(CommonSteps.multipartText("content", content))
            .multiPart("score", starScore)
            .multiPart(CommonSteps.multipartText("productName",productName));
        List<MultiPartSpecification> multiPartSpecList;
        multiPartSpecList = 리뷰_이미지_파일정보_생성();

        for(MultiPartSpecification multiPartSpecification : multiPartSpecList){
            request.multiPart(multiPartSpecification);
        }

        request.when()
            .post("/sns/reviews")
            .then()
            .log().all();

        List<Review> reviewList = reviewRepository.findReviewsByProductUrl(productURL);
        return reviewList.get(reviewList.size()-1).getReviewId();
    }

    public Long SNS_리뷰_댓글_작성(String token, long reviewId){

        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("X-AUTH-TOKEN",token)
                .pathParam("reviewId", reviewId)
                .body(SnsReviewSteps.댓글_작성정보_생성())
                .when()
                .post("/sns/comments/{reviewId}")
                .then()
                .log().all();

        List<Comment> recentComments = commentRepository.findByReview_ReviewId(reviewId);
        return recentComments.get(recentComments.size()-1).getCommentId();
    }

    public void SNS_리액션_추가(String token, long registeredReviewId){
        given()
                .header("X-AUTH-TOKEN", token)
                .pathParam("reviewId", registeredReviewId)
                .param("reaction",reactionContent)
                .when()
                .post("/sns/review-reaction/{reviewId}")
                .then()
                .log().all();
    }


    public void 아이템정보생성() {
        RelatedProduct relatedProduct = RelatedProduct.builder()
            .productUrl("https://www.test.com")
            .name("페로로쉐 초콜릿")
            .price(10000)
            .imagePath("https://www.test.com/image")
            .build();
        ItemDetail itemDetail = ItemDetail.builder()
            .relatedProduct(relatedProduct)
            .detailInfo("페로로쉐 초콜릿 상세정보")
            .build();
        relatedProductRepository.save(relatedProduct);
        itemDetailRepository.save(itemDetail);
    }
}