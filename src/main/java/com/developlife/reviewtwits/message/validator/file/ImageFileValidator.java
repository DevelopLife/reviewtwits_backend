package com.developlife.reviewtwits.message.validator.file;

import com.developlife.reviewtwits.message.annotation.file.ImageFile;
import com.developlife.reviewtwits.type.ReferenceType;
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
        return ReferenceType.isValidFileType(ReferenceType.IMAGE, List.of(value));
    }
}