package com.developlife.reviewtwits.message.annotation.statistics;

import com.developlife.reviewtwits.message.validator.statistics.DeviceValidator;

import javax.validation.Constraint;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DeviceValidator.class)
public @interface Device {
    String message() default "디바이스 정보는 MOBILE,PC 중 하나로만 입력해야 합니다.";
    Class[] groups() default {};
    Class[] payload() default {};
}
