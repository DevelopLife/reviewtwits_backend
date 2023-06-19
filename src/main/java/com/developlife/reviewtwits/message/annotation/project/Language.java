package com.developlife.reviewtwits.message.annotation.project;

import com.developlife.reviewtwits.message.validator.project.LanguageValidator;

import javax.validation.Constraint;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = LanguageValidator.class)
public @interface Language {
    String message() default "Language 형식이 아닙니다. ex) 한국어, ENGLISH";

    Class[] groups() default {};

    Class[] payload() default {};
}
