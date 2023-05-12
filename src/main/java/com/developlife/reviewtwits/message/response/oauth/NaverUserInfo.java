package com.developlife.reviewtwits.message.response.oauth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

/**
 * @author ghdic
 * @since 2023/03/14
 */
// resultcode, message, response
@NoArgsConstructor
@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class NaverUserInfo implements OauthUserInfo {
    private String resultcode;
    private String message;
    private NaverResponse response;

    @Override
    public String sub() {
        return response.id();
    }

    @Override
    public String email() {
        return response.email();
    }

    @Override
    public String picture() {
        return response.profile_image();
    }


}
