package com.developlife.reviewtwits.message.validator.file;

import com.developlife.reviewtwits.message.annotation.file.ImageFile;
import com.developlife.reviewtwits.message.annotation.file.ImageFiles;
import com.developlife.reviewtwits.type.FileReferenceType;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

/**
 * @author WhalesBob
 * @since 2023-03-24
 */
public class ImageFileValidator implements ConstraintValidator<ImageFile, MultipartFile> {

    @Override
    public boolean isValid(MultipartFile value, ConstraintValidatorContext context) {
        if(value == null){
            return true;
        }
        return FileReferenceType.isValidFileType("image", List.of(value));
    }
}