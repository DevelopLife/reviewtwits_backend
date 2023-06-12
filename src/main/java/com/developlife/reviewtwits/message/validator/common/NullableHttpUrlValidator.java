package com.developlife.reviewtwits.message.validator.common;

import com.developlife.reviewtwits.message.annotation.common.NullableHttpUrl;
import com.developlife.reviewtwits.type.UrlChecker;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author WhalesBob
 * @since 2023-05-30
 */
public class NullableHttpUrlValidator implements ConstraintValidator<NullableHttpUrl, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(value == null || value.isEmpty()){
            return true;
        }
        return UrlChecker.isValidUrl(value);
    }
}