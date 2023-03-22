package com.developlife.reviewtwits.message.annotation.user;


import com.developlife.reviewtwits.message.validator.user.BirthdayValidator;

import javax.validation.Constraint;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = BirthdayValidator.class)
public @interface Birthday {
    String message() default "생년월일은 현재 날짜보다 이전 날짜여야 하며 yyyy-MM-dd 형식이어야 합니다.";
    Class[] groups() default {};

    Class[] payload() default {};
}
