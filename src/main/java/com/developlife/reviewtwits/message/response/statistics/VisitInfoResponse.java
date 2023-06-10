package com.developlife.reviewtwits.message.response.statistics;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * @author WhalesBob
 * @since 2023-04-22
 */
@Setter
@Getter
@Builder
public class VisitInfoResponse{
    private String timeStamp;
    private Integer visitCount;
    private Integer previousCompare;
}