package com.developlife.reviewtwits.controller;

import com.developlife.reviewtwits.config.security.JwtTokenProvider;
import com.developlife.reviewtwits.entity.FileInfo;
import com.developlife.reviewtwits.entity.User;
import com.developlife.reviewtwits.exception.user.TokenInvalidException;
import com.developlife.reviewtwits.message.request.ImageUpdateRequest;
import com.developlife.reviewtwits.message.request.user.LoginUserRequest;
import com.developlife.reviewtwits.message.request.user.RegisterUserInfoRequest;
import com.developlife.reviewtwits.message.request.user.RegisterUserRequest;
import com.developlife.reviewtwits.message.response.user.JwtTokenResponse;
import com.developlife.reviewtwits.message.response.user.UserDetailInfoResponse;
import com.developlife.reviewtwits.message.response.user.UserInfoResponse;
import com.developlife.reviewtwits.service.FileStoreService;
import com.developlife.reviewtwits.service.user.UserService;
import com.developlife.reviewtwits.type.JwtProvider;
import com.developlife.reviewtwits.type.ReferenceType;
import com.developlife.reviewtwits.type.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Set;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final FileStoreService fileStoreService;

    @Autowired
    public UserController(UserService userService, JwtTokenProvider jwtTokenProvider, FileStoreService fileStoreService) {
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.fileStoreService = fileStoreService;
    }

    @PostMapping(value = "/login", consumes = "application/json", produces = "application/json")
    public JwtTokenResponse login(@RequestBody @Valid LoginUserRequest loginUserRequest, HttpServletResponse response) {
        User user = userService.login(loginUserRequest);
        jwtTokenProvider.setRefreshTokenForClient(response, user);

        return jwtTokenProvider.issueJwtTokenResponse(user);
    }

    @PostMapping(value = "logout", consumes = "application/json", produces = "application/json")
    public void logout(@AuthenticationPrincipal User user, HttpServletResponse response) {
        if(user != null) {
            userService.logout(user);
        }
        jwtTokenProvider.removeRefreshTokenForClient(response);
    }

    @PostMapping(value = "/register", consumes = "application/json", produces = "application/json")
    @ResponseStatus(value = HttpStatus.CREATED)
    public JwtTokenResponse register(@RequestBody @Valid RegisterUserRequest registerUserRequest, HttpServletResponse response) {
        User user = userService.register(registerUserRequest, Set.of(UserRole.USER));
        jwtTokenProvider.setRefreshTokenForClient(response, user);

        return jwtTokenProvider.issueJwtTokenResponse(user);
    }

    @PostMapping(value = "/register-addition", consumes = "multipart/form-data", produces = "application/json")
    @ResponseStatus(value = HttpStatus.OK)
    public UserDetailInfoResponse registerAddition(@AuthenticationPrincipal User user,
                                             @Valid @ModelAttribute RegisterUserInfoRequest registerUserInfoRequest) {
        return userService.registerAddition(registerUserInfoRequest, user);
    }

    @PostMapping(value = "/issue/access-token", produces = "application/json")
    public JwtTokenResponse issueAccessToken(@CookieValue(required = false) String refreshToken) {
        if(refreshToken == null) {
            throw new TokenInvalidException("refresh token이 없습니다. 로그인 후 진행해주세요");
        }

        return JwtTokenResponse.builder()
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

    @PostMapping(value = "/save-profile-image", consumes = "multipart/form-data", produces = "application/json")
    public String saveProfileImage(@AuthenticationPrincipal User user, @Valid @ModelAttribute ImageUpdateRequest request){

        return userService.saveProfileImage(user, request.imageFile());

    }


    @PostMapping("/change-detail-messages")
    public UserInfoResponse changeDetailMessageOfUserProfile(@AuthenticationPrincipal User user,
                                                 @RequestBody String detailInfo){
        return userService.changeDetailIntroduce(user, detailInfo);
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


    public static String getTokenOwner() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}
