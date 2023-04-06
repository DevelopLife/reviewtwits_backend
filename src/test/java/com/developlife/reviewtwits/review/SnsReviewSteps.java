package com.developlife.reviewtwits.review;

import com.developlife.reviewtwits.message.request.review.SnsCommentWriteRequest;
import io.restassured.builder.MultiPartSpecBuilder;
import io.restassured.specification.MultiPartSpecification;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

/**
 * @author WhalesBob
 * @since 2023-03-31
 */
public class SnsReviewSteps {
    final static String productName = "리뷰제품제품";
    final static String productURL = "http://www.example.com/123";
    final static String rightReviewText = "맛있고 좋아요! 어쩌구저쩌구.... 그랬어요!";
    final static String wrongReviewText = "좋아요";
    final static int starScore = 4;
    final static String commentContent = "테스트 코멘트";
    final static String changeCommentContent = "테스트를 위한 수정 코멘트";
    final static String reactionContent = "GOOD";

    public static List<MultiPartSpecification> 리뷰_이미지_파일정보_생성() {
        try{
            String fileFullName = "image.png";
            File file = new File(System.getProperty("java.io.tmpdir"), fileFullName);

            BufferedImage image = new BufferedImage(200,200,BufferedImage.TYPE_INT_ARGB);
            ImageIO.write(image,"png",file);
            return createMultipartFileList(file);
        }catch (IOException e){
            e.printStackTrace();
            return null;
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


}