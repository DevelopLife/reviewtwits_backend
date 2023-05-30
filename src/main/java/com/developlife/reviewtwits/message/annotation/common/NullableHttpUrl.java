package com.developlife.reviewtwits.message.annotation.common;

import com.developlife.reviewtwits.message.validator.common.NullableHttpUrlValidator;

import javax.validation.Constraint;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NullableHttpUrlValidator.class)
public @interface NullableHttpUrl {
    String message() default "http 혹은 https 로 시작하는 인터넷 페이지 URL 형식이 아닙니다.";
    Class[] groups() default {};
    Class[] payload() default {};
}
