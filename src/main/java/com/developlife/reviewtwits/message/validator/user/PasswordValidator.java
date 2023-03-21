package com.developlife.reviewtwits.message.validator.user;

import com.developlife.reviewtwits.message.annotation.user.Password;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author ghdic
 * @since 2023/03/21
 */
public class PasswordValidator implements ConstraintValidator<Password, String> {

        @Override
        public boolean isValid(String value, ConstraintValidatorContext context) {
            if (value == null) {
                return true;
            }
            return value.matches("^(?=.*[a-zA-Z])(?=.*[!@#$%^*+=-])(?=.*[0-9]).{6,}$");
        }
}
