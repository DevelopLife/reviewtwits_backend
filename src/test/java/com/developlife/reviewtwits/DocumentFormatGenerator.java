package com.developlife.reviewtwits;

import org.assertj.core.util.Lists;
import org.springframework.restdocs.constraints.Constraint;
import org.springframework.restdocs.snippet.Attributes;

import java.util.Collections;
import java.util.List;

/**
 * @author ghdic
 * @since 2023/03/08
 */
public class DocumentFormatGenerator {

    public static Attributes.Attribute getDateFormat() {
        return Attributes.key("format").value("yyyy-MM-DD");
    }

    public static Attributes.Attribute getPhoneNumberFormat() {
        return Attributes.key("format").value("010xxxxxxxx");
    }

    public static Attributes.Attribute getGenderFormat() {
        return Attributes.key("format").value("남자, 여자");
    }
    public static Attributes.Attribute required() {
        List<Constraint> constraints = Lists.newArrayList(new Constraint("javax.validation.constraints.NotNull", Collections.emptyMap()));
        return Attributes.key("validationConstraints").value(constraints);
    }
}
