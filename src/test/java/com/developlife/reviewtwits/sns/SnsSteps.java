package com.developlife.reviewtwits.sns;

import com.developlife.reviewtwits.message.request.sns.FollowRequest;
import org.springframework.stereotype.Component;

/**
 * @author WhalesBob
 * @since 2023-03-20
 */

@Component
public class SnsSteps {

    public static final String userAccountId = "test@naver.com";
    public static final String targetUserAccountId = "whalesbob@naver.com";
    public static final String notExistAccountId = "asdf@naver.com";

    public FollowRequest 팔로우정보_생성(){
        return FollowRequest.builder()
                .targetUserAccountId(targetUserAccountId)
                .build();
    }

    public FollowRequest 팔로우정보_상대방측_생성(){
        return FollowRequest.builder()
                .targetUserAccountId(userAccountId)
                .build();
    }

    public FollowRequest 없는상대방_팔로우요청_생성(){
        return FollowRequest.builder()
                .targetUserAccountId(notExistAccountId)
                .build();
    }

    public FollowRequest 이메일형식아닌_팔로우요청_생성(){
        return FollowRequest.builder()
                .targetUserAccountId("whalesbob")
                .build();
    }
}