package com.developlife.reviewtwits.message.annotation.project;

import com.developlife.reviewtwits.message.validator.project.ColorValidator;
import com.developlife.reviewtwits.message.validator.project.URIValidator;

import javax.validation.Constraint;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ColorValidator.class)
public @interface Color {
    String message() default "Color 형식이 아닙니다. ex) #FFFFFF";

    Class[] groups() default {};

    Class[] payload() default {};
}
