package com.developlife.reviewtwits.message.validator.review;

import com.developlife.reviewtwits.message.annotation.review.ReviewApprove;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author WhalesBob
 * @since 2023-05-19
 */
public class ReviewApproveValidator  implements ConstraintValidator<ReviewApprove, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(value == null){
            return false;
        }
        return value.equals("APPROVED") || value.equals("SPAM");
    }
}