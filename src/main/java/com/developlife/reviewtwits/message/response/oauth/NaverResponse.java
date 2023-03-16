package com.developlife.reviewtwits.message.response.oauth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;

/**
 * @author ghdic
 * @since 2023/03/14
 */
// id, email, gender, birthday, birthyear, mobile
@JsonIgnoreProperties(ignoreUnknown = true)
public record NaverResponse(
    String id,
    String nickname,
    String name,
    String email,
    String gender,
    String age,
    String birthday,
    String profile_image,
    String birthyear,
    String mobile
) {
    @Builder
    public NaverResponse {
    }
}
