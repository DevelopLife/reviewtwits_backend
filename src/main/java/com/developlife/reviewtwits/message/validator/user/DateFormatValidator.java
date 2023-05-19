package com.developlife.reviewtwits.message.validator.user;

import com.developlife.reviewtwits.message.annotation.common.DateFormat;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * @author ghdic
 * @since 2023/03/21
 */
public class DateFormatValidator implements ConstraintValidator<DateFormat, String> {

        @Override
        public boolean isValid(String value, ConstraintValidatorContext context) {
            if (value == null) {
                return true;
            }
            try {
                LocalDate inputPast = LocalDate.parse(value, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                LocalDate now = LocalDate.now();

                if (inputPast.isAfter(now)) {
                    return false;
                }
            } catch (DateTimeParseException e) {
                return false;
            }

            return true;
        }
}
