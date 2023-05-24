package com.developlife.reviewtwits.controller;

import com.developlife.reviewtwits.entity.User;
import com.developlife.reviewtwits.message.annotation.review.ValidReaction;
import com.developlife.reviewtwits.message.request.review.SnsCommentWriteRequest;
import com.developlife.reviewtwits.message.request.review.SnsReviewChangeRequest;
import com.developlife.reviewtwits.message.request.review.SnsReviewWriteRequest;
import com.developlife.reviewtwits.message.response.review.CommentLikeResultResponse;
import com.developlife.reviewtwits.message.response.review.CommentResponse;
import com.developlife.reviewtwits.message.response.review.DetailReactionResponse;
import com.developlife.reviewtwits.message.response.review.ReviewScrapResultResponse;
import com.developlife.reviewtwits.message.response.sns.DetailSnsReviewResponse;
import com.developlife.reviewtwits.service.SnsReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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
    public DetailSnsReviewResponse writeSnsReview(@Valid @ModelAttribute SnsReviewWriteRequest request,
                               @AuthenticationPrincipal User user){

        return snsReviewService.saveSnsReview(request, user);
    }

    @GetMapping("/feeds")
    public List<DetailSnsReviewResponse> getSnsReviewFeeds(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) Long reviewId,
            @RequestParam int size){
        return snsReviewService.getSnsReviews(user,reviewId, size);
    }

    @GetMapping("/reviews/{reviewId}")
    public DetailSnsReviewResponse getOneSnsReview(@AuthenticationPrincipal User user, @PathVariable long reviewId){
        return snsReviewService.getOneSnsReview(user, reviewId);
    }

    @DeleteMapping("/reviews/{reviewId}")
    public DetailSnsReviewResponse deleteSnsReview(@AuthenticationPrincipal User user,
                                @PathVariable Long reviewId){
        snsReviewService.checkReviewCanEdit(user, reviewId);
        return snsReviewService.deleteSnsReview(reviewId);
    }

    @PatchMapping("/reviews/{reviewId}")
    public DetailSnsReviewResponse changeSnsReview(@AuthenticationPrincipal User user,
                                @PathVariable Long reviewId,
                                @Valid @ModelAttribute SnsReviewChangeRequest request){

        snsReviewService.checkReviewCanEdit(user, reviewId);
        return snsReviewService.changeSnsReview(reviewId, request);
    }


    @GetMapping("/comments/{reviewId}")
    public List<CommentResponse> getCommentsForReviews(@AuthenticationPrincipal User user,@PathVariable Long reviewId){
        return snsReviewService.getCommentInfo(reviewId, user);
    }

    @PostMapping("/comments/{reviewId}")
    public CommentResponse writeCommentOnSnsReview(@AuthenticationPrincipal User user,
                                        @PathVariable long reviewId,
                                        @Valid @RequestBody SnsCommentWriteRequest request) {
        return snsReviewService.saveComment(user, reviewId, request);
    }

    @DeleteMapping("/comments/{commentId}")
    public CommentResponse deleteCommentsOnSnsReview(@AuthenticationPrincipal User user,
                                          @PathVariable long commentId){

        return snsReviewService.deleteComment(user,commentId);
    }

    @PatchMapping(value= "/comments/{commentId}",consumes = "application/json")
    public CommentResponse changeCommentsOnSnsReview(@AuthenticationPrincipal User user,
                                          @PathVariable long commentId,
                                            @RequestParam @NotBlank String content){

        return snsReviewService.changeComment(user, commentId, content);
    }

    @PostMapping(value = "/review-reaction/{reviewId}")
    public DetailReactionResponse reactionProcess(@AuthenticationPrincipal User user,
                                                  @PathVariable long reviewId,
                                                  @RequestParam @ValidReaction String reaction
    ){

        return snsReviewService.reactionOnReview(user, reviewId, reaction);
    }

//    @DeleteMapping("/review-reaction/{reviewId}")
//    public DetailReactionResponse deleteReactions(@AuthenticationPrincipal User user,
//                                @PathVariable long reviewId) {
//        return snsReviewService.deleteReactionOnReview(user, reviewId);
//    }
    @GetMapping("/scrap-reviews")
    public List<DetailSnsReviewResponse> getReviewScrapListOfUser(@AuthenticationPrincipal User user){
        return snsReviewService.getReviewsInUserScrap(user);
    }

    @PostMapping("/scrap-reviews/{reviewId}")
    public ReviewScrapResultResponse addReviewScrap(@AuthenticationPrincipal User user,
                                                    @PathVariable long reviewId){

        return snsReviewService.addReviewScrap(user, reviewId);
    }

    @DeleteMapping("/scrap-reviews/{reviewId}")
    public ReviewScrapResultResponse deleteReviewScrap(@AuthenticationPrincipal User user,
                                  @PathVariable long reviewId){

        return snsReviewService.deleteReviewScrap(user, reviewId);
    }

    @PostMapping("/comments-like/{commentId}")
    public CommentLikeResultResponse addLikeOnComments(@AuthenticationPrincipal User user,
                                                       @PathVariable Long commentId){
        return snsReviewService.addLikeOnComment(user,commentId);
    }

    @DeleteMapping("/comments-like/{commentId}")
    public CommentLikeResultResponse deleteLikeOnComments(@AuthenticationPrincipal User user,
                                     @PathVariable Long commentId){
        return snsReviewService.deleteLikeOnComment(user,commentId);
    }
}