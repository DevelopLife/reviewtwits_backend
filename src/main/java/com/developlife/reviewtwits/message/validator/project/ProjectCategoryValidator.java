package com.developlife.reviewtwits.message.validator.project;

import com.developlife.reviewtwits.message.annotation.project.ProjectCategory;

import javax.validation.ConstraintValidator;

/**
 * @author ghdic
 * @since 2023/03/21
 */
public class ProjectCategoryValidator implements ConstraintValidator<ProjectCategory, String> {
    @Override
    public boolean isValid(String value, javax.validation.ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        try {
            com.developlife.reviewtwits.type.project.ProjectCategory.valueOf(value);
        } catch (IllegalArgumentException ex) {
            return false;
        }

        return true;
    }
}
