package com.developlife.reviewtwits.message.response.oauth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * @author ghdic
 * @since 2023/03/14
 */

// 현재 받아와지는 정보 sub, email, email_verified
@NoArgsConstructor
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class KakaoUserInfo implements OauthUserInfo {
    private String sub;
    private String name;
    private String nickname;
    private String picture;
    private String email;
    private String email_verified;
    private String gender;
    private String birthday;
    private Boolean phone_number_verified;

    @Override
    public String sub() {
        return sub;
    }

    @Override
    public String email() {
        return email;
    }
}
