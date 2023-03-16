package com.developlife.reviewtwits.controller;


import com.developlife.reviewtwits.entity.Review;
import com.developlife.reviewtwits.entity.User;
import com.developlife.reviewtwits.exception.product.ProductNotFoundException;
import com.developlife.reviewtwits.message.request.review.ReviewProductURLRequest;
import com.developlife.reviewtwits.message.request.review.ShoppingMallReviewWriteRequest;
import com.developlife.reviewtwits.message.response.review.ShoppingMallReviewProductResponse;
import com.developlife.reviewtwits.service.ShoppingMallReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;

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

    @PostMapping(value = "/shopping", consumes = "multipart/form-data")
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
}