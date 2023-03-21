package com.developlife.reviewtwits.message.validator.user;

import com.developlife.reviewtwits.message.annotation.user.Gender;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author ghdic
 * @since 2023/03/21
 */
public class GenderValidator implements ConstraintValidator<Gender, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        try {
            com.developlife.reviewtwits.type.Gender.valueOf(value);
        } catch (IllegalArgumentException ex) {
            return false;
        }

        return true;
    }
}
