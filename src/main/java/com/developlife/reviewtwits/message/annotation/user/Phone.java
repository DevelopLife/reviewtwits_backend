package com.developlife.reviewtwits.message.annotation.user;

import com.developlife.reviewtwits.message.validator.user.PhoneValidator;

import javax.validation.Constraint;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PhoneValidator.class)
public @interface Phone {
    String message() default "전화번호 형식이 올바르지 않습니다.";

    Class[] groups() default {};

    Class[] payload() default {};
}
