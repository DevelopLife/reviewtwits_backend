package com.developlife.reviewtwits.service.email;

import com.developlife.reviewtwits.repository.EmailVerifyRepository;
import com.developlife.reviewtwits.type.EmailType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.transaction.Transactional;
import java.io.UnsupportedEncodingException;

/**
 * @author ghdic
 * @since 2023/03/08
 */
@Component
public class EmailLinkSender extends EmailSender {

    @Autowired
    public EmailLinkSender(JavaMailSender emailSender, EmailVerifyRepository emailVerifyRepository) {
        super(emailSender, emailVerifyRepository);
    }

    protected MimeMessage createMessage(final String to, final String url) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = emailSender.createMimeMessage();

        message.addRecipients(MimeMessage.RecipientType.TO, to);//보내는 대상
        message.setSubject("Reviewtwits 비밀번호 초기화 메일입니다");//제목

        String msgg="";
        msgg+= "<div style='margin:20px;'>";
        msgg+= "<h1> 안녕하세요 ReviewTwit의 비밀번호 초기화 메일입니다 </h1>";
        msgg+= "<br>";
        msgg+= "<p>아래 링크를 클릭 해주세요<p>";
        msgg+= "<br>";
        msgg+= "<p>감사합니다.<p>";
        msgg+= "<br>";
        msgg+= "<div align='center' style='border:1px solid black; font-family:verdana';>";
        msgg+= "<h3 style='color:blue;'>회원가입 링크입니다.</h3>";
        msgg+= "<div style='font-size:130%'>";
        msgg+= "링크 : <strong><a href='http://localhost:3000/reset-password?code=%s'>비밀번호 초기화하기</a></strong><div><br/>".formatted(url);
        msgg+= "</div>";
        message.setText(msgg, "utf-8", "html");//내용
        message.setFrom(new InternetAddress("ghdic77@gmail.com","reviewtwit"));//보내는 사람

        return message;
    }

    @Override
    public void sendMessage(String to) throws MessagingException, UnsupportedEncodingException {
        sendMessage(to, EmailType.비밀번호찾기코드);
    }
}
