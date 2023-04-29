package com.developlife.reviewtwits.message.validator.statistics;

import com.developlife.reviewtwits.message.annotation.statistics.HttpURL;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

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
        return Pattern.matches("((https?:\\/\\/)|(\\/)|(..\\/))(\\w+:{0,1}\\w*@)?(\\S+)(:[0-9]+)?(\\/|\\/([\\w#!:.?+=&%@!\\-\\/]))?", value);
    }
}