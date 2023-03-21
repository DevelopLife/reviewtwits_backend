package com.developlife.reviewtwits.message.validator.oauth;

import com.developlife.reviewtwits.message.annotation.oauth.JwtProvider;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author ghdic
 * @since 2023/03/21
 */
public class JwtProviderValidator implements ConstraintValidator<JwtProvider, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        try {
            com.developlife.reviewtwits.type.JwtProvider.valueOf(value);
        } catch (IllegalArgumentException ex) {
            return false;
        }

        return true;
    }
}
