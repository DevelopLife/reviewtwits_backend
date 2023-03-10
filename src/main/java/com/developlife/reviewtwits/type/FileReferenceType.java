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

    IMAGE(List.of("jpg","png","svg","gif")),
    USER(IMAGE.filetypeList),
    REVIEW(IMAGE.filetypeList),
    PRODUCT(IMAGE.filetypeList),
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

    public static boolean isValidFileType(String referenceType, String fileName){
        FileReferenceType fileType = FileReferenceType.valueOf(referenceType.toUpperCase(Locale.ROOT));
        String ext = FileStoreService.extractExt(fileName);
        return fileType.filetypeList.contains(ext);
    }

    public static String getContentType(String fileName){
        String ext = FileStoreService.extractExt(fileName);
        for(FileReferenceType type : FileReferenceType.values()){
            if(type.filetypeList.contains(ext)){
                return type.name().toLowerCase(Locale.ROOT) + "/" + ext;
            }
        }
        return "";
    }
}
