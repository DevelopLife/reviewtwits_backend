package com.developlife.reviewtwits.message.validator.user;

import com.developlife.reviewtwits.message.annotation.user.Birthday;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * @author ghdic
 * @since 2023/03/21
 */
public class BirthdayValidator implements ConstraintValidator<Birthday, String> {

        @Override
        public boolean isValid(String value, ConstraintValidatorContext context) {
            if (value == null) {
                return true;
            }
            try {
                LocalDate birthday = LocalDate.parse(value, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                LocalDate now = LocalDate.now();

                if (birthday.isAfter(now)) {
                    return false;
                }
            } catch (DateTimeParseException e) {
                return false;
            }

            return true;
        }
}
