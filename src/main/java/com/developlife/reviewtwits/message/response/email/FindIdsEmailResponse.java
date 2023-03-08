package com.developlife.reviewtwits.message.response.email;

import lombok.Builder;

/**
 * @author ghdic
 * @since 2023/03/08
 */
public record FindIdsEmailResponse(String nickname, String accountId, String createdDate) {
    @Builder
    public FindIdsEmailResponse {
    }
}
