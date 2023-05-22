package com.developlife.reviewtwits.message.annotation.review;

import com.developlife.reviewtwits.message.validator.review.ReviewApproveValidator;

import javax.validation.Constraint;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

@Target({FIELD, PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ReviewApproveValidator.class)
public @interface ReviewApprove {

    String message() default "리뷰 허가요청은 APPROVED, SPAM 중 하나로 입력해야 합니다";
    Class[] groups() default {};
    Class[] payload() default {};
}
