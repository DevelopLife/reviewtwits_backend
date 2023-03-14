package com.developlife.reviewtwits.controller;

import com.developlife.reviewtwits.config.security.JwtTokenProvider;
import com.developlife.reviewtwits.entity.User;
import com.developlife.reviewtwits.message.response.user.JwtTokenResponse;
import com.developlife.reviewtwits.message.request.user.LoginUserRequest;
import com.developlife.reviewtwits.message.request.user.RegisterUserRequest;
import com.developlife.reviewtwits.message.response.user.UserDetailInfoResponse;
import com.developlife.reviewtwits.message.response.user.UserInfoResponse;
import com.developlife.reviewtwits.service.user.UserService;
import com.developlife.reviewtwits.type.JwtProvider;
import com.developlife.reviewtwits.type.UserRole;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Set;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public UserController(UserService userService, JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping(value = "/login", consumes = "application/json", produces = "application/json")
    public JwtTokenResponse login(@RequestBody LoginUserRequest loginUserRequest) {
        User user = userService.login(loginUserRequest);
        // setRefreshTokenForClient(response, user);

        return jwtTokenProvider.issueJwtTokenResponse(user);
    }

//    @PostMapping(value = "logout", consumes = "application/json", produces = "application/json")
//    public void logout(@CookieValue(required = false) String refreshToken, HttpServletResponse response) {
//        if(refreshToken != null) {
//            userService.logout(refreshToken);
//        }
//        removeRefreshTokenForClient(response);
//    }

    @PostMapping(value = "logout")
    public void logout(
        @RequestHeader(required = false, name = "X-REFRESH-TOKEN")
        String refreshToken,
        HttpServletResponse response) {
        if(refreshToken != null) {
            userService.logout(refreshToken);
        }
        // removeRefreshTokenForClient(response);
    }

    private void setRefreshTokenForClient(HttpServletResponse response, User user) {
        // Create a cookie
        Cookie cookie = new Cookie("refreshToken", jwtTokenProvider.issueRefreshToken(user.getAccountId()));
        cookie.setMaxAge((int) JwtTokenProvider.refreshTokenValidTime);
        // 보안 설정
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        cookie.setPath("/");

        response.addCookie(cookie);
    }

    private void removeRefreshTokenForClient(HttpServletResponse response) {
        // Create a cookie
        Cookie cookie = new Cookie("refreshToken", null);
        cookie.setMaxAge(0);
        // 보안 설정
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        cookie.setPath("/");

        response.addCookie(cookie);
    }

    @PostMapping(value = "/register", consumes = "application/json", produces = "application/json")
    @ResponseStatus(value = HttpStatus.CREATED)
    public JwtTokenResponse register(@Valid @RequestBody RegisterUserRequest registerUserRequest, HttpServletResponse response) {
        User user = userService.register(registerUserRequest, Set.of(UserRole.USER));
        // setRefreshTokenForClient(response, user);

        return jwtTokenProvider.issueJwtTokenResponse(user);
    }

    @PostMapping(value = "/issue/access-token", produces = "application/json")
    public JwtTokenResponse issueAccessToken(
        @RequestHeader(name = "X-REFRESH-TOKEN")
        String refreshToken) {
        return JwtTokenResponse.builder()
                .refreshToken(refreshToken)
                .accessToken(jwtTokenProvider.reissueAccessToken(refreshToken))
                .tokenType("Bearer")
                .provider(JwtProvider.REVIEWTWITS)
                .build();
    }

    @GetMapping(value = "/search/{userId}", produces = "application/json")
    public UserInfoResponse searchUser(@PathVariable long userId) {
        return userService.getUserInfo(userId);
    }

    @GetMapping(value = "/me", produces = "application/json")
    public UserDetailInfoResponse me(@AuthenticationPrincipal User user) {
        return userService.getDetailUserInfo(user);
    }

    // admin권한 부여를 받을수 있는 테스트용 메소드
//    @PostMapping(value = "/permission", produces = "application/json")
    public User addAdminPermission() {
        String accountId = getTokenOwner();
        return userService.grantedAdminPermission(accountId);
    }

//    @DeleteMapping(value = "/permission", produces = "application/json")
    public User deleteAdminPermission() {
        String accountId = getTokenOwner();
        return userService.confiscatedAdminPermission(accountId);
    }

//    @GetMapping(value = "/admin", produces = "application/json")
    public String admin() {
        return "hello admin";
    }


    private String getTokenOwner() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}
