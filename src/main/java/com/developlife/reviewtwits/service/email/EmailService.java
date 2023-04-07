package com.developlife.reviewtwits.service.email;

import com.developlife.reviewtwits.entity.EmailVerify;
import com.developlife.reviewtwits.entity.User;
import com.developlife.reviewtwits.exception.mail.MailSendException;
import com.developlife.reviewtwits.exception.mail.NoContentMatchInfoException;
import com.developlife.reviewtwits.exception.mail.NotFoundMatchInfoException;
import com.developlife.reviewtwits.exception.mail.VerifyCodeException;
import com.developlife.reviewtwits.mapper.CommonMapper;
import com.developlife.reviewtwits.mapper.UserMapper;
import com.developlife.reviewtwits.message.request.email.FindIdsEmailRequest;
import com.developlife.reviewtwits.message.request.email.FindPwEmailRequest;
import com.developlife.reviewtwits.message.request.email.ResetPwEmailRequest;
import com.developlife.reviewtwits.message.response.email.FindIdsEmailResponse;
import com.developlife.reviewtwits.repository.EmailVerifyRepository;
import com.developlife.reviewtwits.repository.UserRepository;
import com.developlife.reviewtwits.type.EmailType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author ghdic
 * @since 2023/02/28
 */
@Service
public class EmailService {
    final UserRepository userRepository;
    final UserMapper userMapper;
    final CommonMapper commonMapper;
    final EmailVerifyRepository emailVerifyRepository;
    final EmailCodeSender emailCodeSender;
    final EmailLinkSender emailLinkSender;
    final PasswordEncoder passwordEncoder;

    public EmailService(UserRepository userRepository, UserMapper userMapper, CommonMapper commonMapper, EmailVerifyRepository emailVerifyRepository, EmailCodeSender emailCodeSender, EmailLinkSender emailLinkSender, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.commonMapper = commonMapper;
        this.emailVerifyRepository = emailVerifyRepository;
        this.emailCodeSender = emailCodeSender;
        this.emailLinkSender = emailLinkSender;
        this.passwordEncoder = passwordEncoder;
    }


    public void verifyCodeMessage(String email) {
        try {
            emailCodeSender.sendMessage(email);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new MailSendException("메일 전송에 실패했습니다. 다시 시도해주세요.");
        }
    }

    private void resetPasswordMessage(String email) {
        try {
            emailLinkSender.sendMessage(email);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new MailSendException("메일 전송에 실패했습니다. 다시 시도해주세요.");
        }
    }

    @Transactional(readOnly = true)
    public List<FindIdsEmailResponse> findIdsWithInfo(FindIdsEmailRequest findIdsEmailRequest) {
       List<User> userList =  userRepository.findByPhoneNumberAndBirthDate(
               findIdsEmailRequest.phoneNumber(),
               commonMapper.toDate(findIdsEmailRequest.birthDate()));
       if(userList.isEmpty()) {
           throw new NoContentMatchInfoException("입력하신 개인정보로 가입된 계정이 존재하지 않습니다.");
       }
        return userMapper.toFindIdsEmailResponseList(userList);
    }

    @Transactional
    public void findPwWithInfo(FindPwEmailRequest findPwEmailRequest) {
        userRepository.findByAccountIdAndPhoneNumberAndBirthDate(
                findPwEmailRequest.accountId(),
                findPwEmailRequest.phoneNumber(),
                commonMapper.toDate(findPwEmailRequest.birthDate()))
                .orElseThrow(() -> new NotFoundMatchInfoException("입력하신 개인정보가 일치하지 않습니다"));
        resetPasswordMessage(findPwEmailRequest.accountId());
    }

    @Transactional
    public void resetPwWithInfo(ResetPwEmailRequest resetPwEmailRequest) {
        EmailVerify emailVerify = emailVerifyRepository.findByVerifyCodeAndType(resetPwEmailRequest.verifyCode(), EmailType.비밀번호찾기코드)
                .orElseThrow(() -> new VerifyCodeException("인증코드가 일치하지 않습니다."));
        LocalDateTime expiredDate = emailVerify.getVerifyDate().plusHours(1);
        if(LocalDateTime.now().isAfter(expiredDate)) {
            throw new VerifyCodeException("인증코드가 만료되었습니다.");
        }
        if(emailVerify.isAlreadyUsed()) {
            throw new VerifyCodeException("이미 사용된 인증코드입니다.");
        }
        if(emailVerify.getVerifyCode().equals(resetPwEmailRequest.verifyCode())) {
            emailVerify.setAlreadyUsed(true);
            emailVerifyRepository.save(emailVerify);
        } else {
            throw new VerifyCodeException("인증코드가 일치하지 않습니다.");
        }

        User user = userRepository.findByAccountId(emailVerify.getEmail())
                .orElseThrow(() -> new NotFoundMatchInfoException("입력하신 코드와 이메일에 이상이 있습니다. 다시 코드를 발급해주세요"));
        user.setAccountPw(passwordEncoder.encode(resetPwEmailRequest.accountPw()));
        userRepository.save(user);
    }
}
