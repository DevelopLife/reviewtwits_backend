package com.developlife.reviewtwits.controller;

import com.developlife.reviewtwits.service.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author ghdic
 * @since 2023/02/28
 */
@RestController
@RequestMapping("/email")
public class EmailController {
    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @GetMapping("/verify")
    public ResponseEntity<Void> verifyEmail(@RequestParam String email) throws Exception {
        emailService.sendSimpleMessage(email);
        return ResponseEntity.ok().build();
    }
}
