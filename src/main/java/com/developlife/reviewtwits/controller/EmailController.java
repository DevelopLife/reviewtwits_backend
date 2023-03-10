package com.developlife.reviewtwits.controller;

import com.developlife.reviewtwits.message.request.email.FindIdsEmailRequest;
import com.developlife.reviewtwits.message.request.email.FindPwEmailRequest;
import com.developlife.reviewtwits.message.request.email.ResetPwEmailRequest;
import com.developlife.reviewtwits.message.response.email.FindIdsEmailResponse;
import com.developlife.reviewtwits.service.email.EmailService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author ghdic
 * @since 2023/02/28
 */
@RestController
@RequestMapping("/emails")
public class EmailController {
    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @GetMapping(value = "/verify", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public void verifyEmail(@RequestParam String accountId) {
        emailService.verifyCodeMessage(accountId);
    }

    @PostMapping("/find-ids")
    public List<FindIdsEmailResponse> findIds(@RequestBody FindIdsEmailRequest findIdsEmailRequest) {
        return emailService.findIdsWithInfo(findIdsEmailRequest);
    }

    @PostMapping("/find-password")
    public void findPw(@RequestBody FindPwEmailRequest findPwEmailRequest) {
        emailService.findPwWithInfo(findPwEmailRequest);
    }

    @PostMapping("/reset-password")
    public void resetPw(@RequestBody ResetPwEmailRequest resetPwEmailRequest) {
        emailService.resetPwWithInfo(resetPwEmailRequest);
    }
}
