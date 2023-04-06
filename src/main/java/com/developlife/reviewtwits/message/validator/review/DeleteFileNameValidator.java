package com.developlife.reviewtwits.message.validator.review;

import com.developlife.reviewtwits.message.annotation.review.DeleteFileName;
import com.developlife.reviewtwits.type.FileReferenceType;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

/**
 * @author WhalesBob
 * @since 2023-04-06
 */
public class DeleteFileNameValidator implements ConstraintValidator<DeleteFileName, List<String>> {
    @Override
    public boolean isValid(List<String> value, ConstraintValidatorContext context) {
        if(value == null || value.isEmpty()){
            return true;
        }
        return FileReferenceType.isValidDeleteFileName("image",value);
    }
}