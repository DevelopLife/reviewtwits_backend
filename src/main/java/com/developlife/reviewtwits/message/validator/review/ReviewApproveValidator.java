package com.developlife.reviewtwits.message.validator.review;

import com.developlife.reviewtwits.message.annotation.review.ReviewApprove;
import com.developlife.reviewtwits.type.review.ReviewStatus;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author WhalesBob
 * @since 2023-05-19
 */
public class ReviewApproveValidator  implements ConstraintValidator<ReviewApprove, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        try{
            ReviewStatus.valueOf(value);
            return true;
        }catch(IllegalArgumentException e){
            return value != null && value.equals("ALL");
        }
    }
}