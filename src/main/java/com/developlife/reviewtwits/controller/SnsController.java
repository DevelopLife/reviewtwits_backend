package com.developlife.reviewtwits.controller;

import com.developlife.reviewtwits.entity.User;
import com.developlife.reviewtwits.message.request.sns.FollowRequest;
import com.developlife.reviewtwits.service.SnsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @author WhalesBob
 * @since 2023-03-20
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/sns")
public class SnsController {

    private final SnsService snsService;

    @PostMapping("/requestFollow")
    public void followProcess(@AuthenticationPrincipal User user, @Valid @RequestBody FollowRequest request){
        snsService.followProcess(user, request.targetUserAccountId());
    }

    @PostMapping("/requestUnfollow")
    public void unfollowProcess(@AuthenticationPrincipal User user, @Valid @RequestBody FollowRequest request){
        snsService.unfollowProcess(user, request.targetUserAccountId());
    }
}