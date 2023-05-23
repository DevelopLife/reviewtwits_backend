package com.developlife.reviewtwits.type.review;

public enum ReviewStatus {
    PENDING("PENDING"),
    APPROVED("APPROVED"),
    SPAM("SPAM"),
    DELETED("DELETED");

    private String status;

    ReviewStatus(String status) {
        this.status = status;
    }
}
