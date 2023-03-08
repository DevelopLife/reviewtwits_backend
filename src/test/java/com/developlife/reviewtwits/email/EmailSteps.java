package com.developlife.reviewtwits.email;

import com.developlife.reviewtwits.message.request.email.FindIdsEmailRequest;
import com.developlife.reviewtwits.user.UserSteps;

/**
 * @author ghdic
 * @since 2023/03/08
 */
public class EmailSteps {
    public static FindIdsEmailRequest 아이디찾기_아이디존재_생성() {
        return FindIdsEmailRequest.builder()
                .phoneNumber(UserSteps.phoneNumber)
                .birthDate(UserSteps.birthDate)
                .build();
    }

    public static FindIdsEmailRequest 아이디찾기_아이디없음_생성() {
        return FindIdsEmailRequest.builder()
                .phoneNumber("01099999999")
                .birthDate(UserSteps.birthDate)
                .build();
    }
}
