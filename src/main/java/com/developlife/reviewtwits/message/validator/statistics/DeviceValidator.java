package com.developlife.reviewtwits.message.validator.statistics;

import com.developlife.reviewtwits.message.annotation.statistics.Device;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author WhalesBob
 * @since 2023-04-23
 */
public class DeviceValidator implements ConstraintValidator<Device, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(value == null || value.isEmpty()){
            return false;
        }
        return value.equals("MOBILE") || value.equals("PC");
    }
}