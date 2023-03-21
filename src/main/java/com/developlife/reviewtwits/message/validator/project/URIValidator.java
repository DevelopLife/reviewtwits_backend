package com.developlife.reviewtwits.message.validator.project;

import com.developlife.reviewtwits.message.annotation.project.URI;

import javax.validation.ConstraintValidator;

/**
 * @author ghdic
 * @since 2023/03/21
 */
public class URIValidator implements ConstraintValidator<URI, String> {
    @Override
    public boolean isValid(String value, javax.validation.ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        String[] parseURIs = value.split(",");
        for (String parseURI : parseURIs) {
            parseURI = parseURI.trim();
            if (!parseURI.matches("^(?!.*\\/\\/)[a-zA-Z0-9/-]+$")) {
                return false;
            }
        }

        return true;
    }
}
