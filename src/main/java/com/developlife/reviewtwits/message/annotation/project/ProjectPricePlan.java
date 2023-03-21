package com.developlife.reviewtwits.message.annotation.project;

import com.developlife.reviewtwits.message.validator.project.ProjectPricePlanValidator;
import com.developlife.reviewtwits.message.validator.project.URIValidator;

import javax.validation.Constraint;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ProjectPricePlanValidator.class)
public @interface ProjectPricePlan {
    String message() default "FREE_PLAN, PLUS_PLAN, PRO_PLAN, BUSINESS_PLAN 중 하나의 값을 입력해주세요.";

    Class[] groups() default {};

    Class[] payload() default {};
}
