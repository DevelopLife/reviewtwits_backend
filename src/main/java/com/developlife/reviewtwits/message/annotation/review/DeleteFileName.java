package com.developlife.reviewtwits.message.annotation.review;


import com.developlife.reviewtwits.message.validator.review.DeleteFileNameValidator;

import javax.validation.Constraint;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DeleteFileNameValidator.class)
public @interface DeleteFileName {

    String message() default "지울 파일 이름은 이미지 확장자 형식으로 입력해야 합니다.";
    Class[] groups() default {};
    Class[] payload() default {};
}
