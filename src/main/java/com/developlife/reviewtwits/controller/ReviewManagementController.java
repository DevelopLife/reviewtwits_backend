package com.developlife.reviewtwits.controller;

import com.developlife.reviewtwits.entity.User;
import com.developlife.reviewtwits.message.request.review.ReviewApproveRequest;
import com.developlife.reviewtwits.message.response.review.ReviewApproveResponse;
import com.developlife.reviewtwits.service.ReviewManageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author WhalesBob
 * @since 2023-05-17
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/review-management")
public class ReviewManagementController {

    private final ReviewManageService reviewManageService;

    @PostMapping("/approve")
    public ReviewApproveResponse approveReview(@AuthenticationPrincipal User user, @Valid @RequestBody ReviewApproveRequest request){

        return reviewManageService.reviewAuthorizeProcess(user, request);
    }
}