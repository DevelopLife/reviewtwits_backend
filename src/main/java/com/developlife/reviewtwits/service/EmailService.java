package com.developlife.reviewtwits.service;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.transaction.Transactional;

import com.developlife.reviewtwits.entity.EmailVerify;
import com.developlife.reviewtwits.entity.User;
import com.developlife.reviewtwits.exception.mail.NotFoundMatchInfoException;
import com.developlife.reviewtwits.mapper.CommonMapper;
import com.developlife.reviewtwits.mapper.UserMapper;
import com.developlife.reviewtwits.message.request.email.FindIdsEmailRequest;
import com.developlife.reviewtwits.message.response.email.FindIdsEmailResponse;
import com.developlife.reviewtwits.repository.EmailVerifyRepository;
import com.developlife.reviewtwits.repository.UserRepository;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

/**
 * @author ghdic
 * @since 2023/02/28
 */
@Service
public class EmailService {

    final JavaMailSender emailSender;
    final EmailVerifyRepository emailVerifyRepository;
    final UserRepository userRepository;
    final UserMapper userMapper;
    final CommonMapper commonMapper;

    public EmailService(JavaMailSender emailSender, EmailVerifyRepository emailVerifyRepository, UserRepository userRepository, UserMapper userMapper, CommonMapper commonMapper) {
        this.emailSender = emailSender;
        this.emailVerifyRepository = emailVerifyRepository;
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.commonMapper = commonMapper;
    }

    private MimeMessage createMessage(final String to, final String authenticationCode)throws Exception{
        System.out.println("보내는 대상 : " + to);
        System.out.println("인증 번호 : " + authenticationCode);
        MimeMessage message = emailSender.createMimeMessage();

        message.addRecipients(MimeMessage.RecipientType.TO, to);//보내는 대상
        message.setSubject("이메일 인증 테스트");//제목

        String msgg="";
        msgg+= "<div style='margin:20px;'>";
        msgg+= "<h1> 안녕하세요 ReviewTwit의 회원가입 인증메일입니다 </h1>";
        msgg+= "<br>";
        msgg+= "<p>아래 코드를 복사해 입력해주세요<p>";
        msgg+= "<br>";
        msgg+= "<p>감사합니다.<p>";
        msgg+= "<br>";
        msgg+= "<div align='center' style='border:1px solid black; font-family:verdana';>";
        msgg+= "<h3 style='color:blue;'>회원가입 인증 코드입니다.</h3>";
        msgg+= "<div style='font-size:130%'>";
        msgg+= "CODE : <strong>";
        msgg+= authenticationCode+"</strong><div><br/> ";
        msgg+= "</div>";
        message.setText(msgg, "utf-8", "html");//내용
        message.setFrom(new InternetAddress("ghdic77@gmail.com","reviewtwit"));//보내는 사람

        return message;
    }

    public static String createKey() {
        StringBuffer key = new StringBuffer();
        Random rnd = new Random();

        for (int i = 0; i < 8; i++) { // 인증코드 8자리
            int index = rnd.nextInt(3); // 0~2 까지 랜덤

            switch (index) {
                case 0:
                    key.append((char) ((int) (rnd.nextInt(26)) + 97));
                    //  a~z  (ex. 1+97=98 => (char)98 = 'b')
                    break;
                case 1:
                    key.append((char) ((int) (rnd.nextInt(26)) + 65));
                    //  A~Z
                    break;
                case 2:
                    key.append((rnd.nextInt(10)));
                    // 0~9
                    break;
            }
        }
        return key.toString();
    }

    @Transactional
    public void sendMessage(String to)throws Exception {
        String key = createKey();
        emailVerifyRepository.findByEmail(to).ifPresentOrElse(
                emailVerify -> {
                    emailVerify.setVerifyCode(key);
                    emailVerify.setAlreadyUsed(false);
                    emailVerifyRepository.save(emailVerify);
                },
                () -> {
                    EmailVerify emailVerify = EmailVerify.builder()
                            .email(to)
                            .verifyCode(key)
                            .build();
                    emailVerifyRepository.save(emailVerify);
                }
        );

        MimeMessage message = createMessage(to, key);
        try{
            emailSender.send(message);
        }catch(MailException es){
            throw new MailSendException("메일 발송 실패");
        }
    }

    @Transactional
    public String sendEmailMock(String to) {
        String key = createKey();
        emailVerifyRepository.findByEmail(to).ifPresentOrElse(
                emailVerify -> {
                    emailVerify.setVerifyCode(key);
                    emailVerify.setAlreadyUsed(false);
                    emailVerifyRepository.save(emailVerify);
                },
                () -> {
                    EmailVerify emailVerify = EmailVerify.builder()
                            .email(to)
                            .verifyCode(key)
                            .build();
                    emailVerifyRepository.save(emailVerify);
                }
        );
        return key;
    }

    public List<FindIdsEmailResponse> findIdsWithInfo(FindIdsEmailRequest findIdsEmailRequest) {
       List<User> userList =  userRepository.findByPhoneNumberAndBirthDate(
               findIdsEmailRequest.phoneNumber(),
               commonMapper.toDate(findIdsEmailRequest.birthDate()));
       if(userList.isEmpty()) {
           throw new NotFoundMatchInfoException("입력하신 개인정보로 가입된 계정이 존재하지 않습니다.");
       }
        return userMapper.toFindIdsEmailResponseList(userList);
    }
}
