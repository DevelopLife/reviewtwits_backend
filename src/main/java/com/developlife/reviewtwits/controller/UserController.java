package com.developlife.reviewtwits.controller;

import com.developlife.reviewtwits.config.security.JwtTokenProvider;
import com.developlife.reviewtwits.entity.User;
import com.developlife.reviewtwits.message.request.LoginUserRequest;
import com.developlife.reviewtwits.message.request.RegisterUserRequest;
import com.developlife.reviewtwits.service.UserService;
import com.developlife.reviewtwits.type.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Set;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public UserController(UserService userService, JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @GetMapping(value = "/login", consumes = "application/json", produces = "application/json")
    public String login(@RequestBody LoginUserRequest loginUserRequest) {
        User user = userService.login(loginUserRequest);
        return jwtTokenProvider.createToken(user.getAccountId(), user.getRoles());
    }

    @PostMapping(value = "", consumes = "application/json", produces = "application/json")
    public User register(@RequestBody RegisterUserRequest registerUserRequest) {
        return userService.register(registerUserRequest, Set.of(UserRole.USER));
    }

    @GetMapping(value = "/me", produces = "application/json")
    public User me() {
        // SecurityContext에서 인증받은 회원의 정보를 가져옴
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String id = authentication.getName();
        return userService.getUser(id);
    }

    // admin권한 부여를 받을수 있는 테스트용 메소드
    @PostMapping(value = "/permission", produces = "application/json")
    public User addAdminPermission() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String accountId = authentication.getName();
        return userService.grantedAdminPermission(accountId);
    }

    @DeleteMapping(value = "/permission", produces = "application/json")
    public User deleteAdminPermission() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String accountId = authentication.getName();
        return userService.confiscatedAdminPermission(accountId);
    }

    @GetMapping(value = "/admin", produces = "application/json")
    public String admin() {
        return "hello admin";
    }
}
