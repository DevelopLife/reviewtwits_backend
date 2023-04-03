package com.developlife.reviewtwits.message.annotation.review;

import com.developlife.reviewtwits.message.validator.review.ReactionValidator;

import javax.validation.Constraint;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.ElementType.TYPE_USE;

@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ReactionValidator.class)
public @interface ValidReaction {

    String message() default "존재하지 않는 리액션입니다.";
    Class[] groups() default {};
    Class[] payload() default {};
}
