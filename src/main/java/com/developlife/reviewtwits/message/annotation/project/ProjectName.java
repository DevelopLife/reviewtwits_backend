package com.developlife.reviewtwits.message.annotation.project;

import com.developlife.reviewtwits.message.validator.project.ProjectNameValidator;

import javax.validation.Constraint;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ProjectNameValidator.class)
public @interface ProjectName {

    String message() default "프로젝트 이름은 영어, 숫자, '-', '_'만 입력 가능합니다";
    Class[] groups() default {};
    Class[] payload() default {};
}
