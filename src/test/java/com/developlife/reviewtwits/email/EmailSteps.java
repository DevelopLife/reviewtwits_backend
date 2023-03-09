package com.developlife.reviewtwits.email;

import com.developlife.reviewtwits.message.request.email.FindIdsEmailRequest;
import com.developlife.reviewtwits.message.request.email.FindPwEmailRequest;
import com.developlife.reviewtwits.message.request.email.ResetPwEmailRequest;
import com.developlife.reviewtwits.service.email.EmailLinkSender;
import com.developlife.reviewtwits.type.EmailType;
import com.developlife.reviewtwits.user.UserSteps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author ghdic
 * @since 2023/03/08
 */
@Component
public class EmailSteps {

    @Autowired
    private EmailLinkSender emailLinkSender;

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

    public static FindPwEmailRequest 비밀번호찾기_이메일보내기성공_요청생성() {
        return FindPwEmailRequest.builder()
                .accountId(UserSteps.accountId)
                .phoneNumber(UserSteps.phoneNumber)
                .birthDate(UserSteps.birthDate)
                .build();
    }

    public ResetPwEmailRequest 비밀번호재설정_비밀번호설정_요청생성() {
        String key = emailLinkSender.storageVerifyInfo(UserSteps.accountId, EmailType.비밀번호찾기코드);
        return ResetPwEmailRequest.builder()
                .accountPw("changepw123!")
                .verifyCode(key)
                .build();
    }
}
