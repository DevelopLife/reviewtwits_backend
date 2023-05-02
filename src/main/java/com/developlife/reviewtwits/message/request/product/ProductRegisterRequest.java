package com.developlife.reviewtwits.message.request.product;

import com.developlife.reviewtwits.message.annotation.statistics.HttpURL;

import javax.validation.constraints.Min;

public record ProductRegisterRequest(
        @HttpURL
        String productUrl,
        @Min(value = 1, message = "프로젝트 아이디는 1 이상의 수로 입력해야 합니다.")
        long projectId) {
}
