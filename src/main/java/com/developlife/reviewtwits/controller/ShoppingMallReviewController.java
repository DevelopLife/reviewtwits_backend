package com.developlife.reviewtwits.controller;


import com.developlife.reviewtwits.entity.User;
import com.developlife.reviewtwits.exception.product.ProductNotFoundException;
import com.developlife.reviewtwits.message.request.review.ReviewProductURLRequest;
import com.developlife.reviewtwits.message.request.review.ShoppingMallReviewChangeRequest;
import com.developlife.reviewtwits.message.request.review.ShoppingMallReviewWriteRequest;
import com.developlife.reviewtwits.message.response.review.DetailReviewResponse;
import com.developlife.reviewtwits.message.response.review.ShoppingMallReviewProductResponse;
import com.developlife.reviewtwits.service.ShoppingMallReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.io.IOException;
import java.util.List;

/**
 * @author WhalesBob
 * @since 2023-03-13
 */

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ShoppingMallReviewController {

    private final ShoppingMallReviewService reviewService;

    @PostMapping(value = "/shopping", consumes = "multipart/form-data;charset=UTF-8")
    public void writeShoppingMallReview(@Valid @ModelAttribute ShoppingMallReviewWriteRequest request,
                                        @AuthenticationPrincipal User user) throws IOException {
        reviewService.saveShoppingMallReview(request, user);
    }

    @GetMapping(value = "/shopping", produces = "application/json")
    public ShoppingMallReviewProductResponse getShoppingMallReviewInfo(@Valid @RequestBody ReviewProductURLRequest request){
        ShoppingMallReviewProductResponse result =  reviewService.findShoppingMallReviewTotalInfo(request.productURL());

        if(result == null){
            throw new ProductNotFoundException("입력한 URL 로 등록된 제품이 존재하지 않습니다");
        }
        return result;
    }

    @DeleteMapping(value = "/shopping/{reviewId}")
    public void deleteShoppingMallReview(@NotBlank @PathVariable Long reviewId,
                                         @AuthenticationPrincipal User user){

        reviewService.checkReviewCanEdit(user,reviewId);
        reviewService.deleteShoppingMallReview(reviewId);
    }

    @PatchMapping(value = "/shopping/{reviewId}", consumes = "multipart/form-data;charset=UTF-8")
    public void changeShoppingMallReview(@PathVariable Long reviewId,
                                         @AuthenticationPrincipal User user,
                                         @Valid @ModelAttribute ShoppingMallReviewChangeRequest request) throws IOException {

        reviewService.checkReviewCanEdit(user, reviewId);
        reviewService.changeShoppingMallReview(reviewId, request);
    }

    @PutMapping(value = "/shopping/{reviewId}")
    public void restoreShoppingMallReview(@PathVariable Long reviewId,
                                          @AuthenticationPrincipal User user){

        reviewService.checkReviewCanEdit(user,reviewId);
        reviewService.restoreShoppingMallReview(reviewId);
    }

    @GetMapping(value = "/shopping/list", produces = "application/json;charset=UTF-8")
    public List<DetailReviewResponse> findShoppingMallReviewList(@Valid @RequestBody ReviewProductURLRequest request){
        return reviewService.findShoppingMallReviewList(request.productURL());
    }
}