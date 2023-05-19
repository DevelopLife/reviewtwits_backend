package com.developlife.reviewtwits.message.annotation.user;

import com.developlife.reviewtwits.message.validator.user.GenderValidator;

import javax.validation.Constraint;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = GenderValidator.class)
public @interface Gender {
    String message() default "성별은 남자 또는 여자만 입력 가능합니다.";
    Class[] groups() default {};

    Class[] payload() default {};
}
