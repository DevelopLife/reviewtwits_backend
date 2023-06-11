package com.developlife.reviewtwits.type;

import java.util.regex.Pattern;

/**
 * @author WhalesBob
 * @since 2023-06-11
 */
public class UrlChecker {
    public static final String URL_REGEX = "(http(s)?:\\/\\/|www.)([a-z0-9\\w]+\\.*)+[a-z0-9]{2,4}(:\\d+)?([\\/a-z0-9-%#?&=\\w])+(\\.[a-z0-9]{2,4}(\\?[\\/a-z0-9-%#?&=\\w]+)*)*";

    public static boolean isValidUrl(String url) {
        return Pattern.matches(URL_REGEX, url);
    }
}