package com.developlife.reviewtwits.service.email;

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
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
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

    public EmailService(UserRepository userRepository, UserMapper userMapper, CommonMapper commonMapper, EmailVerifyRepository emailVerifyRepository, EmailCodeSender emailCodeSender, EmailLinkSender emailLinkSender) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.commonMapper = commonMapper;
        this.emailVerifyRepository = emailVerifyRepository;
        this.emailCodeSender = emailCodeSender;
        this.emailLinkSender = emailLinkSender;
    }


    public void verifyCodeMessage(String email) {
        try {
            emailCodeSender.sendMessage(email);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new MailSendException("?????? ????????? ??????????????????. ?????? ??????????????????.");
        }
    }

    private void resetPasswordMessage(String email) {
        try {
            emailLinkSender.sendMessage(email);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new MailSendException("?????? ????????? ??????????????????. ?????? ??????????????????.");
        }
    }

    public List<FindIdsEmailResponse> findIdsWithInfo(FindIdsEmailRequest findIdsEmailRequest) {
       List<User> userList =  userRepository.findByPhoneNumberAndBirthDate(
               findIdsEmailRequest.phoneNumber(),
               commonMapper.toDate(findIdsEmailRequest.birthDate()));
       if(userList.isEmpty()) {
           throw new NoContentMatchInfoException("???????????? ??????????????? ????????? ????????? ???????????? ????????????.");
       }
        return userMapper.toFindIdsEmailResponseList(userList);
    }

    public void findPwWithInfo(FindPwEmailRequest findPwEmailRequest) {
        userRepository.findByAccountIdAndPhoneNumberAndBirthDate(
                findPwEmailRequest.accountId(),
                findPwEmailRequest.phoneNumber(),
                commonMapper.toDate(findPwEmailRequest.birthDate()))
                .orElseThrow(() -> new NotFoundMatchInfoException("???????????? ??????????????? ???????????? ????????????"));
        resetPasswordMessage(findPwEmailRequest.accountId());
    }


    public void resetPwWithInfo(ResetPwEmailRequest resetPwEmailRequest) {
        emailVerifyRepository.findByVerifyCode(resetPwEmailRequest.verifyCode())
                .orElseThrow(() -> new VerifyCodeException("??????????????? ???????????? ????????????."));
    }
}
