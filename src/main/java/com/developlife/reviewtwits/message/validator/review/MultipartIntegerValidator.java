package com.developlife.reviewtwits.message.validator.review;

import com.developlife.reviewtwits.message.annotation.review.MultipartInteger;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author WhalesBob
 * @since 2023-03-13
 */
public class MultipartIntegerValidator implements ConstraintValidator<MultipartInteger, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        try{
            int integerValue = Integer.parseInt(value);
            if((double)integerValue != Double.parseDouble(value)){
                return false;
            }
            return 0 <= integerValue && integerValue <= 5;
        }catch(NumberFormatException ex){
            return false;
        }
    }
}
