package com.developlife.reviewtwits.message.annotation.project;

import com.developlife.reviewtwits.message.validator.project.URIValidator;

import javax.validation.Constraint;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = URIValidator.class)
public @interface URI {
    String message() default "URI 형식이 아닙니다. ex) \"/projects, /products\"";

    Class[] groups() default {};

    Class[] payload() default {};
}
