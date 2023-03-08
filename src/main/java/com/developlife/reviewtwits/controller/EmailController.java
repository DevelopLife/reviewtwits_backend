package com.developlife.reviewtwits.controller;

import com.developlife.reviewtwits.message.request.email.FindIdsEmailRequest;
import com.developlife.reviewtwits.message.response.email.FindIdsEmailResponse;
import com.developlife.reviewtwits.service.EmailService;
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

    @GetMapping("/verify")
    public ResponseEntity<Void> verifyEmail(@RequestParam String email) throws Exception {
        emailService.sendMessage(email);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/find-ids")
    public List<FindIdsEmailResponse> findIds(@RequestBody FindIdsEmailRequest findIdsEmailRequest) {
        return emailService.findIdsWithInfo(findIdsEmailRequest);
    }
}
