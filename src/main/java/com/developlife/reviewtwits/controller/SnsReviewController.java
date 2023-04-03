package com.developlife.reviewtwits.controller;

import com.developlife.reviewtwits.entity.User;
import com.developlife.reviewtwits.message.annotation.review.ValidReaction;
import com.developlife.reviewtwits.message.request.review.SnsCommentWriteRequest;
import com.developlife.reviewtwits.message.request.review.SnsReviewChangeRequest;
import com.developlife.reviewtwits.message.request.review.SnsReviewWriteRequest;
import com.developlife.reviewtwits.message.response.review.CommentResponse;
import com.developlife.reviewtwits.message.response.review.DetailSnsReviewResponse;
import com.developlife.reviewtwits.service.SnsReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @author WhalesBob
 * @since 2023-03-31
 */

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/sns")
@Validated
public class SnsReviewController {

    private final SnsReviewService snsReviewService;

    @PostMapping(value = "/reviews")
    public void writeSnsReview(@Valid @ModelAttribute SnsReviewWriteRequest request,
                               @AuthenticationPrincipal User user){

        snsReviewService.saveSnsReview(request, user);
    }

    @GetMapping("/feeds")
    public List<DetailSnsReviewResponse> getSnsReviewFeeds(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) Long reviewId,
            @RequestParam int size){
        return snsReviewService.getSnsReviews(user,reviewId, size);
    }

    @DeleteMapping("/reviews/{reviewId}")
    public void deleteSnsReview(@AuthenticationPrincipal User user,
                                @PathVariable Long reviewId){
        snsReviewService.checkReviewCanEdit(user, reviewId);
        snsReviewService.deleteSnsReview(reviewId);
    }

    @PatchMapping("/reviews/{reviewId}")
    public void changeSnsReview(@AuthenticationPrincipal User user,
                                @PathVariable Long reviewId,
                                @Valid @ModelAttribute SnsReviewChangeRequest request){

        snsReviewService.checkReviewCanEdit(user, reviewId);
        snsReviewService.changeSnsReview(reviewId, request);
    }


    @GetMapping("/comments/{reviewId}")
    public List<CommentResponse> getCommentsForReviews(@PathVariable Long reviewId){
        return snsReviewService.getCommentInfo(reviewId);
    }

    @PostMapping("/comments/{reviewId}")
    public void writeCommentOnSnsReview(@AuthenticationPrincipal User user,
                                        @PathVariable long reviewId,
                                        @Valid @RequestBody SnsCommentWriteRequest request) {
        snsReviewService.saveComment(user, reviewId, request);
    }

    @DeleteMapping("/comments/{commentId}")
    public void deleteCommentsOnSnsReview(@AuthenticationPrincipal User user,
                                          @PathVariable long commentId){

        snsReviewService.deleteComment(user,commentId);
    }

    @PatchMapping(value= "/comments/{commentId}",consumes = "application/json")
    public String changeCommentsOnSnsReview(@AuthenticationPrincipal User user,
                                          @PathVariable long commentId,
                                            @RequestParam @NotBlank String content){

        return snsReviewService.changeComment(user, commentId, content);

    }

    @PostMapping(value = "/review-reaction/{reviewId}")
    public void addReactions(@AuthenticationPrincipal User user,
                             @PathVariable long reviewId,
                             @RequestParam @ValidReaction String reaction
                             ){

        snsReviewService.addReactionOnReview(user, reviewId, reaction);
    }

    @DeleteMapping("/review-reaction/{reviewId}")
    public void deleteReactions(@AuthenticationPrincipal User user,
                                @PathVariable long reviewId) {
        snsReviewService.deleteReactionOnReview(user, reviewId);
    }
}