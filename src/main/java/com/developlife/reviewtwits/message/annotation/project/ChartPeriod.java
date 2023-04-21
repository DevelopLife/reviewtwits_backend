package com.developlife.reviewtwits.message.annotation.project;

import com.developlife.reviewtwits.message.validator.project.ChartPeriodInputValidator;

import javax.validation.Constraint;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ChartPeriodInputValidator.class)
public @interface ChartPeriod {

    String message() default "시간 데이터 범위는 1d,3d,5d,7d,15d,1mo,3mo,6mo,1y,3y,5y 중 하나로 입력해야 합니다.";
    Class[] groups() default {};
    Class[] payload() default {};
}
