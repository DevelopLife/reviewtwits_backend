package com.developlife.reviewtwits.message.request.review;

import lombok.Builder;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

/**
 * @author WhalesBob
 * @since 2023-04-02
 */
public record SnsCommentWriteRequest(
        @NotBlank(message = "댓글은 비어 있을 수 없습니다.")
        String content,
        @Min(value = 0, message = "댓글 그룹은 음수일 수 없습니다.")
        long parentId
) {

        @Builder
        public SnsCommentWriteRequest{

        }
}