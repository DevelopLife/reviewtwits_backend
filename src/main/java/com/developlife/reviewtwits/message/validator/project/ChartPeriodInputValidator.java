package com.developlife.reviewtwits.message.validator.project;

import com.developlife.reviewtwits.message.annotation.project.ChartPeriod;
import com.developlife.reviewtwits.type.project.ChartPeriodUnit;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author WhalesBob
 * @since 2023-04-21
 */
public class ChartPeriodInputValidator implements ConstraintValidator<ChartPeriod, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(value == null) {
            return false;
        }
        return ChartPeriodUnit.checkChartPeriodInput(value);
    }
}