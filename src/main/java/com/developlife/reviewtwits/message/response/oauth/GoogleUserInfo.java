package com.developlife.reviewtwits.message.response.oauth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

/**
 * @author ghdic
 * @since 2023/03/14
 */
// 현재 받는 정보: sub, name, given_name, family_name, picture, email, email_verified, locale
@NoArgsConstructor
@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class GoogleUserInfo implements OauthUserInfo {
    private String sub;
    private String name;
    private String given_name;
    private String family_name;
    private String picture;
    private String email;
    private Boolean email_verified;
    private String locale;

    @Override
    public String sub() {
        return sub;
    }

    @Override
    public String email() {
        return email;
    }
}
