package com.developlife.reviewtwits.message.request.user;

import lombok.Builder;

/**
 * @author ghdic
 * @since 2023/02/25
 */
@Builder
public record UserInfoRequest(
        Long userId
) {
}
