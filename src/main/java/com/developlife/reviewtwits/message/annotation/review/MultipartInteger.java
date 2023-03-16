package com.developlife.reviewtwits.message.annotation.review;

import com.developlife.reviewtwits.message.validator.review.MultipartIntegerValidator;

import javax.validation.Constraint;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MultipartIntegerValidator.class)
public @interface MultipartInteger {

    String message() default "별점은 0 이상의 5 이하의 정수로 입력되어야 합니다.";
    Class[] groups() default {};
    Class[] payload() default {};
}
