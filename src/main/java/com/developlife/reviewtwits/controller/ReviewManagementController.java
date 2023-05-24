package com.developlife.reviewtwits.controller;

import com.developlife.reviewtwits.entity.User;
import com.developlife.reviewtwits.message.annotation.common.DateFormat;
import com.developlife.reviewtwits.message.annotation.review.ReviewApprove;
import com.developlife.reviewtwits.message.annotation.review.SortDirection;
import com.developlife.reviewtwits.message.request.review.ReviewApproveRequest;
import com.developlife.reviewtwits.message.response.review.DetailShoppingMallReviewResponse;
import com.developlife.reviewtwits.message.response.review.ReviewApproveResponse;
import com.developlife.reviewtwits.service.ReviewManageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

/**
 * @author WhalesBob
 * @since 2023-05-17
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/review-management")
@Validated
public class ReviewManagementController {

    private final ReviewManageService reviewManageService;

    @PostMapping("/approve")
    public ReviewApproveResponse approveReview(@AuthenticationPrincipal User user, @Valid @RequestBody ReviewApproveRequest request){

        return reviewManageService.reviewAuthorizeProcess(user, request);
    }

    @GetMapping("/search")
    public List<DetailShoppingMallReviewResponse> searchReviewByInfo(@AuthenticationPrincipal User user,
                                                                     @Min(value = 0, message = "페이지 숫자는 0 이상 입력해야 합니다.")
                                                                     @RequestParam int page,
                                                                     @Min(value = 1, message = "size 숫자는 1 이상 입력해야 합니다.")
                                                                     @RequestParam int size,
                                                                     @ReviewApprove @RequestParam(required = false, defaultValue = "ALL") String status,
                                                                     @SortDirection @RequestParam(required = false, defaultValue = "NEWEST") String sort,
                                                                     @DateFormat @RequestParam(required = false, defaultValue = "1990-01-01") String startDate,
                                                                     @DateFormat @RequestParam(required = false) String endDate){


        return reviewManageService.searchReviewByInfo(user, page, size, status, sort, startDate, endDate);
    }
}