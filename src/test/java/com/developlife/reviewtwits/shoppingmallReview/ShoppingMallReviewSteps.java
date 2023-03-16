package com.developlife.reviewtwits.shoppingmallReview;

import com.developlife.reviewtwits.entity.Product;
import com.developlife.reviewtwits.entity.Project;
import com.developlife.reviewtwits.mapper.ProjectMapper;
import com.developlife.reviewtwits.project.ProjectSteps;
import com.developlife.reviewtwits.repository.ProductRepository;
import com.developlife.reviewtwits.repository.ProjectRepository;
import io.restassured.builder.MultiPartSpecBuilder;
import io.restassured.specification.MultiPartSpecification;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

/**
 * @author WhalesBob
 * @since 2023-03-13
 */

@Component
public class ShoppingMallReviewSteps {

    final static String productURL = "http://www.example.com/123";
    final static String rightReviewText = "맛있고 좋아요! 어쩌구저쩌구.... 그랬어요!";
    final static String wrongReviewText = "좋아요";
    final static int starScore = 4;

    public static List<MultiPartSpecification> 리뷰_이미지_파일정보_생성() throws IOException {
        String fileFullName = "image.png";
        File file = new File(System.getProperty("java.io.tmpdir"), fileFullName);

        BufferedImage image = new BufferedImage(200,200,BufferedImage.TYPE_INT_ARGB);
        ImageIO.write(image,"png",file);

        return createMultipartFileList(file);
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

    public static Product 임시_상품정보_생성(Project project, ProductRepository repository){
        Product product = Product.builder()
                .productUrl(productURL)
                .project(project)
                .build();

        repository.save(product);
        return product;
    }

    public static Project 임시_프로젝트정보_생성(ProjectMapper mapper, ProjectRepository repository){
        Project project = mapper.toProject(ProjectSteps.프로젝트생성요청_생성());
        repository.save(project);
        return project;
    }


}