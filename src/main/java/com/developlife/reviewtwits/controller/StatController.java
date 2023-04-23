package com.developlife.reviewtwits.controller;

import com.developlife.reviewtwits.entity.User;
import com.developlife.reviewtwits.message.request.StatMessageRequest;
import com.developlife.reviewtwits.message.response.statistics.SaveStatResponse;
import com.developlife.reviewtwits.service.StatService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author WhalesBob
 * @since 2023-04-23
 */

@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
public class StatController {

    private final StatService statService;

    @PostMapping("/visited-info")
    public SaveStatResponse saveVisitedInfo(@AuthenticationPrincipal User user,
                                            @RequestBody StatMessageRequest statMessageRequest){

        return statService.saveStatInfo(user,statMessageRequest);
    }
}