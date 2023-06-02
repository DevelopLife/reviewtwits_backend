package com.developlife.reviewtwits.controller;


import com.developlife.reviewtwits.entity.User;
import com.developlife.reviewtwits.exception.product.ProductNotRegisteredException;
import com.developlife.reviewtwits.message.annotation.review.ShoppingMallReviewSort;
import com.developlife.reviewtwits.message.request.review.ShoppingMallReviewChangeRequest;
import com.developlife.reviewtwits.message.request.review.ShoppingMallReviewWriteRequest;
import com.developlife.reviewtwits.message.response.review.DetailReactionResponse;
import com.developlife.reviewtwits.message.response.review.DetailShoppingMallReviewResponse;
import com.developlife.reviewtwits.message.response.review.ShoppingMallReviewProductResponse;
import com.developlife.reviewtwits.service.ShoppingMallReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

/**
 * @author WhalesBob
 * @since 2023-03-13
 */

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
@Validated
public class ShoppingMallReviewController {

    private final ShoppingMallReviewService reviewService;

    @PostMapping(value = "/shopping", consumes = "multipart/form-data;charset=UTF-8")
    public DetailShoppingMallReviewResponse writeShoppingMallReview(@Valid @ModelAttribute ShoppingMallReviewWriteRequest request,
                                        @AuthenticationPrincipal User user) {
        return reviewService.saveShoppingMallReview(request, user);
    }

    @GetMapping(value = "/shopping", produces = "application/json")
    public ShoppingMallReviewProductResponse getShoppingMallReviewInfo(@RequestHeader String productURL) {
        reviewService.checkProductURLIsValid(productURL);
        ShoppingMallReviewProductResponse result =  reviewService.findShoppingMallReviewTotalInfo(productURL);

        if(result == null){
            throw new ProductNotRegisteredException("입력한 URL 로 등록된 제품이 존재하지 않습니다");
        }
        return result;
    }

    @GetMapping(value = "/shopping/{reviewId}", produces = "application/json")
    public DetailShoppingMallReviewResponse findOneShoppingMallReview(@PathVariable Long reviewId){
        return reviewService.findOneShoppingMallReview(reviewId);
    }


    @DeleteMapping(value = "/shopping/{reviewId}")
    public DetailShoppingMallReviewResponse deleteShoppingMallReview(@PathVariable
                                                                     @Min(value = 1, message = "리뷰 아이디는 1 이상의 수로 입력해야 합니다.")
                                                                     Long reviewId,
                                                                     @AuthenticationPrincipal User user){

        reviewService.checkReviewCanEdit(user,reviewId);
        return reviewService.deleteShoppingMallReview(reviewId);
    }

    @PatchMapping(value = "/shopping/{reviewId}", consumes = "multipart/form-data;charset=UTF-8")
    public DetailShoppingMallReviewResponse changeShoppingMallReview(@PathVariable Long reviewId,
                                         @AuthenticationPrincipal User user,
                                         @Valid @ModelAttribute ShoppingMallReviewChangeRequest request){

        reviewService.checkReviewCanEdit(user, reviewId);
        return reviewService.changeShoppingMallReview(reviewId, request);
    }

    @PutMapping(value = "/shopping/{reviewId}")
    public DetailShoppingMallReviewResponse restoreShoppingMallReview(@PathVariable Long reviewId,
                                          @AuthenticationPrincipal User user){

        reviewService.checkReviewCanEdit(user,reviewId);
        return reviewService.restoreShoppingMallReview(reviewId);
    }

    @GetMapping(value = "/shopping/list", produces = "application/json;charset=UTF-8")
    public List<DetailShoppingMallReviewResponse> findShoppingMallReviewList(@RequestHeader String productURL,
                                                                             @RequestParam @ShoppingMallReviewSort String sort) {
        reviewService.checkProductURLIsValid(productURL);
        return reviewService.findShoppingMallReviewList(productURL, sort);
    }

    @PostMapping(value = "/shopping/like/{reviewId}")
    public DetailReactionResponse shoppingMallReviewLikeProcess(@AuthenticationPrincipal User user,
                                                                @PathVariable @Min(value = 1, message = "리뷰 아이디는 1 이상의 수로 입력해야 합니다.")
                                                                Long reviewId){
        return reviewService.shoppingMallReviewLikeProcess(user, reviewId);
    }
}