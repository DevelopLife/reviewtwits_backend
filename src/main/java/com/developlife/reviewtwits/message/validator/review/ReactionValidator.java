package com.developlife.reviewtwits.message.validator.review;

import com.developlife.reviewtwits.message.annotation.review.ValidReaction;
import com.developlife.reviewtwits.type.ReactionType;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author WhalesBob
 * @since 2023-04-02
 */
public class ReactionValidator implements ConstraintValidator<ValidReaction, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        try {
            ReactionType.valueOf(value);
            return true;
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }
}