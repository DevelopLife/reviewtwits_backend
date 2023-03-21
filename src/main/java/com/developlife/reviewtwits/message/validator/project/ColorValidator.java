package com.developlife.reviewtwits.message.validator.project;

import com.developlife.reviewtwits.message.annotation.project.Color;

import javax.validation.ConstraintValidator;

/**
 * @author ghdic
 * @since 2023/03/21
 */
public class ColorValidator implements ConstraintValidator<Color, String> {
    @Override
    public boolean isValid(String value, javax.validation.ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        return value.matches("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$");
    }
}
