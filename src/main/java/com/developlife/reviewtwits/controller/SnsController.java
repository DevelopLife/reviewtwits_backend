package com.developlife.reviewtwits.controller;

import com.developlife.reviewtwits.entity.User;
import com.developlife.reviewtwits.message.request.sns.FollowRequest;
import com.developlife.reviewtwits.message.response.sns.FollowResultResponse;
import com.developlife.reviewtwits.message.response.sns.ItemResponse;
import com.developlife.reviewtwits.message.response.sns.SearchAllResponse;
import com.developlife.reviewtwits.message.response.sns.SnsReviewResponse;
import com.developlife.reviewtwits.message.response.user.UserInfoResponse;
import com.developlife.reviewtwits.service.SnsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * @author WhalesBob
 * @since 2023-03-20
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/sns")
@Validated
public class SnsController {

    private final SnsService snsService;

    @PostMapping("/request-follow")
    public FollowResultResponse followProcess(@AuthenticationPrincipal User user, @Valid @RequestBody FollowRequest request){
        return snsService.followProcess(user, request.targetUserNickname());
    }

    @PostMapping("/request-unfollow")
    public FollowResultResponse unfollowProcess(@AuthenticationPrincipal User user, @Valid @RequestBody FollowRequest request){
        return snsService.unfollowProcess(user, request.targetUserNickname());
    }

    @GetMapping("/get-followers/{nickname}")
    public List<UserInfoResponse> getFollowers(@PathVariable @NotBlank String nickname){
        return snsService.getFollowerList(nickname);
    }

    @GetMapping("/get-followings/{nickname}")
    public List<UserInfoResponse> getFollowings(@PathVariable @NotBlank String nickname){
        return snsService.getFollowingList(nickname);
    }

    @GetMapping("/search")
    public SearchAllResponse searchAll(@AuthenticationPrincipal User user, @Size(min=2, max=20) @RequestParam String searchKey){
        return snsService.searchAll(searchKey, user);
    }

    @GetMapping("/recommend-product")
    public List<ItemResponse> recommendProduct(){
        return snsService.recommendProduct();
    }

    @GetMapping("/suggest-followers")
    public List<UserInfoResponse> suggestFollowers(@AuthenticationPrincipal User user){
        return snsService.suggestFollowers(user);
    }

    @GetMapping("/profile/{nickname}")
    public UserInfoResponse findUserProfile(@PathVariable String nickname){
        return snsService.findUserProfile(nickname);
    }

    @GetMapping("/profile/reviews/{nickname}")
    public List<SnsReviewResponse> findReviewsOfUser(
            @PathVariable String nickname,
            @RequestParam(required = false) Long reviewId,
            @RequestParam int size){
        return snsService.findReviewsOfUser(nickname, reviewId, size);
    }
}