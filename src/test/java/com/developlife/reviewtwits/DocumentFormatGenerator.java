package com.developlife.reviewtwits;

import org.springframework.restdocs.snippet.Attributes;

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
}
