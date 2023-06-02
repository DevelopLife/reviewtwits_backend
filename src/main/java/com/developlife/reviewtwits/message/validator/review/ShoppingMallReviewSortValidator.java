package com.developlife.reviewtwits.message.validator.review;

import com.developlife.reviewtwits.message.annotation.review.ShoppingMallReviewSort;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author WhalesBob
 * @since 2023-06-02
 */
public class ShoppingMallReviewSortValidator implements ConstraintValidator<ShoppingMallReviewSort, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(value == null || value.isBlank()){
            return false;
        }
        return value.equals("NEWEST") || value.equals("BEST");
    }
}