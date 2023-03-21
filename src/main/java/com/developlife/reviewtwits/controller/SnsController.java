package com.developlife.reviewtwits.controller;

import com.developlife.reviewtwits.entity.User;
import com.developlife.reviewtwits.message.request.sns.FollowRequest;
import com.developlife.reviewtwits.message.response.user.UserInfoResponse;
import com.developlife.reviewtwits.service.SnsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Email;
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
    public void followProcess(@AuthenticationPrincipal User user, @Valid @RequestBody FollowRequest request){
        snsService.followProcess(user, request.targetUserAccountId());
    }

    @PostMapping("/request-unfollow")
    public void unfollowProcess(@AuthenticationPrincipal User user, @Valid @RequestBody FollowRequest request){
        snsService.unfollowProcess(user, request.targetUserAccountId());
    }

    @GetMapping("/get-followers/{accountId}")
    public List<UserInfoResponse> getFollowers(@PathVariable String accountId){
        return List.of();
        //return snsService.getFollowerList(accountId);
    }

    @GetMapping("/get-followings/{accountId}")
    public List<UserInfoResponse> getFollowings(@PathVariable String accountId){
        return List.of();
        //return snsService.getFollowingList(accountId);
    }
}