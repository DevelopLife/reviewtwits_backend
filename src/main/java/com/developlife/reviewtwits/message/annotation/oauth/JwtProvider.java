package com.developlife.reviewtwits.message.annotation.oauth;

import com.developlife.reviewtwits.message.validator.oauth.JwtProviderValidator;

import javax.validation.Constraint;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = JwtProviderValidator.class)
public @interface JwtProvider {
    String message() default "지원하지 않은 제공자입니다";

    Class[] groups() default {};

    Class[] payload() default {};
}
