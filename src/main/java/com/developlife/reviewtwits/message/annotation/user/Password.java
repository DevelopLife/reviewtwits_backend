package com.developlife.reviewtwits.message.annotation.user;

import com.developlife.reviewtwits.message.validator.user.PasswordValidator;

import javax.validation.Constraint;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordValidator.class)
public @interface Password {

    String message() default "비밀번호는 6자리 이상, 영문, 숫자, 특수문자 조합이어야 합니다.";
    Class[] groups() default {};

    Class[] payload() default {};
}
