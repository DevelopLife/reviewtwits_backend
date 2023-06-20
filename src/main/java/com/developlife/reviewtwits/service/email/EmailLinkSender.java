package com.developlife.reviewtwits.service.email;

import com.developlife.reviewtwits.repository.EmailVerifyRepository;
import com.developlife.reviewtwits.type.EmailType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;

/**
 * @author ghdic
 * @since 2023/03/08
 */
@Component
public class EmailLinkSender extends EmailSender {

    @Autowired
    public EmailLinkSender(JavaMailSender emailSender, EmailVerifyRepository emailVerifyRepository, SpringTemplateEngine templateEngine) {
        super(emailSender, emailVerifyRepository, templateEngine);
    }

    protected MimeMessage createMessage(final String to, final String code) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = emailSender.createMimeMessage();

        message.addRecipients(MimeMessage.RecipientType.TO, to);//보내는 대상
        message.setSubject("Reviewtwits 비밀번호 초기화 메일입니다");//제목


        message.setText(setContext("resetPassword", code), "utf-8", "html");//내용
        message.setFrom(new InternetAddress("ghdic77@gmail.com","reviewtwit"));//보내는 사람

        return message;
    }

    @Override
    public void sendMessage(String to) throws MessagingException, UnsupportedEncodingException {
        sendMessage(to, EmailType.비밀번호찾기코드);
    }
}
