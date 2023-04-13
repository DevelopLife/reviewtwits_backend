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
    RELATED_PRODUCT(IMAGE.filetypeList),
    ITEM_DETAIL(IMAGE.filetypeList),
    TEST(List.of("txt","mp4"));

    // enum 을 통해 관리되어야 하는 것
    //  1. 받을 수 있는 파일타입

    private List<String> filetypeList;


    FileReferenceType(List<String> filetypeList) {
        this.filetypeList = filetypeList;
    }

    public static boolean isValidFileType(FileReferenceType referenceType, List<MultipartFile> files){

        for(MultipartFile file : files){
            String ext = FileStoreService.extractExt(file.getOriginalFilename());
            if(!referenceType.filetypeList.contains(ext)){
                return false;
            }
        }
        return true;
    }

    public static boolean isValidDeleteFileName(FileReferenceType referenceType, List<String> deleteFileName){
        for(String name : deleteFileName){
            int position = name.lastIndexOf(".");
            String ext = name.substring(position + 1);
            if(!referenceType.filetypeList.contains(ext)){
                return false;
            }
        }
        return true;
    }

    public static boolean isValidFileType(FileReferenceType referenceType, String fileName){
        String ext = FileStoreService.extractExt(fileName);
        return referenceType.filetypeList.contains(ext);
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
