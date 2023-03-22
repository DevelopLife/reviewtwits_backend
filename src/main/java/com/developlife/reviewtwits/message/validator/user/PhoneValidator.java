package com.developlife.reviewtwits.message.validator.user;

import com.developlife.reviewtwits.message.annotation.user.Phone;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author ghdic
 * @since 2023/03/21
 */
public class PhoneValidator implements ConstraintValidator<Phone, String> {

        @Override
        public boolean isValid(String value, ConstraintValidatorContext context) {
            if (value == null) {
                return true;
            }
            return value.matches("^01(?:0|1|[6-9])(?:\\d{3}|\\d{4})\\d{4}$");
        }
}
