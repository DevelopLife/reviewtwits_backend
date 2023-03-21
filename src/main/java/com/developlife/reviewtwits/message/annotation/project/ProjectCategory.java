package com.developlife.reviewtwits.message.annotation.project;

import com.developlife.reviewtwits.message.validator.project.ProjectCategoryValidator;
import com.developlife.reviewtwits.message.validator.project.URIValidator;

import javax.validation.Constraint;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ProjectCategoryValidator.class)
public @interface ProjectCategory {
    String message() default "ProjectCategory 형식이 아닙니다. ex) 쇼핑, 영화, 게임";

    Class[] groups() default {};

    Class[] payload() default {};
}
