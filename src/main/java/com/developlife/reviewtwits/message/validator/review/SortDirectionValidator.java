package com.developlife.reviewtwits.message.validator.review;

import com.developlife.reviewtwits.message.annotation.review.SortDirection;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author WhalesBob
 * @since 2023-05-19
 */
public class SortDirectionValidator implements ConstraintValidator<SortDirection, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value.equals("NEWEST") || value.equals("OLDEST");
    }
}