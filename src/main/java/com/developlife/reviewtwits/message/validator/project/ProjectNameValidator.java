package com.developlife.reviewtwits.message.validator.project;

import com.developlife.reviewtwits.message.annotation.project.ProjectName;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author WhalesBob
 * @since 2023-06-17
 */
public class ProjectNameValidator implements ConstraintValidator<ProjectName, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value != null && value.matches("[a-zA-Z0-9-_]+");
    }
}