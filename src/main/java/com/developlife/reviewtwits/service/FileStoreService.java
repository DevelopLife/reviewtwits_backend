package com.developlife.reviewtwits.service;

import com.developlife.reviewtwits.entity.FileInfo;
import com.developlife.reviewtwits.entity.FileManager;
import com.developlife.reviewtwits.exception.file.FileEmptyException;
import com.developlife.reviewtwits.exception.file.FileNotStoredException;
import com.developlife.reviewtwits.exception.file.InvalidFilenameExtensionException;
import com.developlife.reviewtwits.repository.FileInfoRepository;
import com.developlife.reviewtwits.repository.FileManagerRepository;
import com.developlife.reviewtwits.type.FileReferenceType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileStoreService {

    @Value("${file.dir}")
    private String fileDir;

    private final FileInfoRepository fileInfoRepository;
    private final FileManagerRepository fileManagerRepository;
    private final AwsS3Service awsService;

    @Transactional
    public FileInfo storeFile(MultipartFile multipartFile) {
        if(multipartFile.isEmpty()){
            return null;
        }

        String originalFilename = multipartFile.getOriginalFilename();
        String storeFilename = createStoreFileName(originalFilename);
        try{
            //log.info("s3에 개별 파일 저장 시도");
            multipartFile.transferTo(new File(getFullPath(storeFilename)));
            //awsService.uploadToAWS(multipartFile, storeFilename);
        }catch(IOException e){
            e.printStackTrace();
            throw new FileNotStoredException("IOException 이 발생했습니다. 알려주세요");
        }
        FileInfo file = FileInfo.builder().filePath(getFullPath(storeFilename)).realFilename(storeFilename)
                        .originalFilename(originalFilename).build();
        fileInfoRepository.save(file);
        return file;
    }

    public List<FileInfo> storeFiles(List<MultipartFile> multipartFiles, Long referenceID, String referenceType) {

        checkFolder();

        if(multipartFiles.get(0).isEmpty()){
            throw new FileEmptyException("파일 내역이 비워져 있습니다.");
        }
        if(!FileReferenceType.isValidFileType(referenceType, multipartFiles)){
            throw new InvalidFilenameExtensionException("올바르지 않는 파일 타입입니다.");
        }

        List<FileInfo> storeFileResult = new ArrayList<>();
        List<FileManager> fileManagerList = new ArrayList<>();

        for (MultipartFile multipartFile : multipartFiles) {
            if(multipartFile != null && !multipartFile.isEmpty()){
                FileInfo fileInfo = storeFile(multipartFile);
                storeFileResult.add(fileInfo);
                fileManagerList.add(new FileManager(fileInfo,referenceID,referenceType));
            }
        }
        fileManagerRepository.saveAll(fileManagerList);

        return storeFileResult;
    }

    public String findOriginalFilename(String storedFileName){
        Optional<FileInfo> fileInfo = fileInfoRepository.findFileInfoByRealFilename(storedFileName);
        return fileInfo.map(FileInfo::getOriginalFilename).orElse(null);
    }

    public String getFullPath(String filename){
        return fileDir + filename;
    }


    private String createStoreFileName(String originalFilename) {
        String ext = extractExt(originalFilename);
        String uuid = UUID.randomUUID().toString();
        return uuid + "." + ext;
    }

    public static String extractExt(String originalFilename) {
        int position = originalFilename.lastIndexOf(".");
        return originalFilename.substring(position + 1);
    }

    public List<String> bringFileNameList(String referenceType, Long referenceID){
        return fileManagerRepository.findRealFileNameByReferenceIdAAndReferenceType(referenceID, referenceType);
    }

    public void checkDeleteFile(List<String> fileNames){
        List<FileInfo> fileInfoList = fileInfoRepository.findFileInfosByRealFilenameIn(fileNames);
        for(FileInfo info : fileInfoList){
            info.setExist(false);
        }
        fileInfoRepository.saveAll(fileInfoList);
    }

    private void checkFolder(){
        File folder = new File(fileDir);
        if(!folder.exists()){
            folder.mkdir();
        }
    }
}
