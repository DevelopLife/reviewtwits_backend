package com.developlife.reviewtwits.type;

import com.developlife.reviewtwits.service.FileStoreService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Locale;

/**
 * @author WhalesBob
 * @since 2023-02-26
 */
public enum FileReferenceType {
    USER(List.of("jpg","png","svg","gif")),
    REVIEW(List.of("jpg","png","svg","gif")),
    PRODUCT(List.of("jpg","png","svg","gif")),
    TEST(List.of("txt"));

    // enum 을 통해 관리되어야 하는 것
    //  1. 받을 수 있는 파일타입

    private List<String> filetypeList;

    FileReferenceType(List<String> filetypeList) {
        this.filetypeList = filetypeList;
    }

    public static boolean isValidFileType(String referenceType, List<MultipartFile> files){
        FileReferenceType fileType = FileReferenceType.valueOf(referenceType.toUpperCase(Locale.ROOT));
        for(MultipartFile file : files){
            String ext = FileStoreService.extractExt(file.getOriginalFilename());
            if(!fileType.filetypeList.contains(ext)){
                return false;
            }
        }
        return true;
    }
}
