package com.developlife.reviewtwits.message.validator.common;

import com.developlife.reviewtwits.message.annotation.common.HttpURL;
import com.developlife.reviewtwits.type.UrlChecker;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author WhalesBob
 * @since 2023-04-23
 */
public class HttpURLValidator implements ConstraintValidator<HttpURL, String>{
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(value == null || value.isEmpty()){
            return false;
        }
        return UrlChecker.isValidUrl(value);
    }
}