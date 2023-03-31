package com.developlife.reviewtwits.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author WhalesBob
 * @since 2023-03-29
 */

@Service
@Slf4j
@RequiredArgsConstructor
@Getter
@PropertySource(value = "s3.properties")
public class AwsS3Service {

    private final AmazonS3 s3Client;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    @Value("${aws.s3.commonUrl}")
    private String url;

    public String uploadToAWS(MultipartFile file, String storeFilename) throws IOException{

        try{
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            PutObjectRequest request = new PutObjectRequest(bucketName, storeFilename, file.getInputStream(), metadata);
            request.withCannedAcl(CannedAccessControlList.AuthenticatedRead);
            s3Client.putObject(request);
            return storeFilename;
        }catch(AmazonServiceException e){
            log.error("uploadToAWS AmazonServiceException error={}", e.getMessage());
        }catch(SdkClientException e){
            log.error("uploadToAWS SdkClientException error={}", e.getMessage());
        }

        return "";
    }

    public Resource getFilesFromS3(String fileName) throws IOException{
        return new UrlResource(url + fileName);
    }

}