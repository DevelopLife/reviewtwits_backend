package com.developlife.reviewtwits.service.email;

import com.developlife.reviewtwits.entity.User;
import com.developlife.reviewtwits.exception.mail.MailSendException;
import com.developlife.reviewtwits.exception.mail.NoContentMatchInfoException;
import com.developlife.reviewtwits.exception.mail.NotFoundMatchInfoException;
import com.developlife.reviewtwits.mapper.CommonMapper;
import com.developlife.reviewtwits.mapper.UserMapper;
import com.developlife.reviewtwits.message.request.email.FindIdsEmailRequest;
import com.developlife.reviewtwits.message.request.email.FindPwEmailRequest;
import com.developlife.reviewtwits.message.response.email.FindIdsEmailResponse;
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
    final EmailCodeSender emailCodeSender;
    final EmailLinkSender emailLinkSender;

    public EmailService(UserRepository userRepository, UserMapper userMapper, CommonMapper commonMapper, EmailCodeSender emailCodeSender, EmailLinkSender emailLinkSender) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.commonMapper = commonMapper;
        this.emailCodeSender = emailCodeSender;
        this.emailLinkSender = emailLinkSender;
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

    public List<FindIdsEmailResponse> findIdsWithInfo(FindIdsEmailRequest findIdsEmailRequest) {
       List<User> userList =  userRepository.findByPhoneNumberAndBirthDate(
               findIdsEmailRequest.phoneNumber(),
               commonMapper.toDate(findIdsEmailRequest.birthDate()));
       if(userList.isEmpty()) {
           throw new NoContentMatchInfoException("입력하신 개인정보로 가입된 계정이 존재하지 않습니다.");
       }
        return userMapper.toFindIdsEmailResponseList(userList);
    }

    public void findPwWithInfo(FindPwEmailRequest findPwEmailRequest) {
        userRepository.findByAccountIdAndPhoneNumberAndBirthDate(
                findPwEmailRequest.accountId(),
                findPwEmailRequest.phoneNumber(),
                commonMapper.toDate(findPwEmailRequest.birthDate()))
                .orElseThrow(() -> new NotFoundMatchInfoException("입력하신 개인정보가 일치하지 않습니다"));
        resetPasswordMessage(findPwEmailRequest.accountId());
    }
}
