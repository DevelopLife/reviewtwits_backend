package com.developlife.reviewtwits.message.annotation.file;

import com.developlife.reviewtwits.message.validator.file.ImagesFileValidator;

import javax.validation.Constraint;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ImagesFileValidator.class)
public @interface ImageFiles {

    String message() default "입력된 파일이 이미지가 아닙니다.";

    Class[] groups() default {};

    Class[] payload() default {};
}
