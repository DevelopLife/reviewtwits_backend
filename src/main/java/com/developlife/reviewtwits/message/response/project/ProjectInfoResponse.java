package com.developlife.reviewtwits.message.response.project;

import lombok.Builder;

public record ProjectInfoResponse(String projectId, String projectName, String projectDescription, String projectColor, String reviewCount, String category) {
    @Builder
    public ProjectInfoResponse {
    }
}
