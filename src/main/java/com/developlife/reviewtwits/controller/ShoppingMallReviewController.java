package com.developlife.reviewtwits.controller;


import com.developlife.reviewtwits.message.request.review.ShoppingMallReviewWriteRequest;
import com.developlife.reviewtwits.service.ShoppingMallReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public void writeShoppingMallReview(@Valid @ModelAttribute ShoppingMallReviewWriteRequest request) throws IOException {
        reviewService.saveShoppingMallReview(request);
    }

}