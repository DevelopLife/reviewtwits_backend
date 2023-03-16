package com.developlife.reviewtwits.message.validator.review;

import com.developlife.reviewtwits.message.annotation.review.ImageFiles;
import com.developlife.reviewtwits.type.FileReferenceType;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

/**
 * @author WhalesBob
 * @since 2023-03-13
 */
public class ImageFileValidator implements ConstraintValidator<ImageFiles,List<MultipartFile>> {

    @Override
    public boolean isValid(List<MultipartFile> value, ConstraintValidatorContext context) {
        if(value == null || value.isEmpty()){
            return true;
        }
        return FileReferenceType.isValidFileType("image",value);
    }
}
