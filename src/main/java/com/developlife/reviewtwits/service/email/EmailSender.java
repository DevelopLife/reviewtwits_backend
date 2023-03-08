package com.developlife.reviewtwits.service.email;

import com.developlife.reviewtwits.entity.EmailVerify;
import com.developlife.reviewtwits.exception.mail.MailSendException;
import com.developlife.reviewtwits.repository.EmailVerifyRepository;
import com.developlife.reviewtwits.type.EmailType;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.transaction.Transactional;
import java.io.UnsupportedEncodingException;
import java.util.Random;

public abstract class EmailSender {

    protected final JavaMailSender emailSender;
    protected final EmailVerifyRepository emailVerifyRepository;

    protected EmailSender(JavaMailSender emailSender, EmailVerifyRepository emailVerifyRepository) {
        this.emailSender = emailSender;
        this.emailVerifyRepository = emailVerifyRepository;
    }

    protected abstract MimeMessage createMessage(String email, String element) throws MessagingException, UnsupportedEncodingException;

    @Transactional
    protected void sendMessage(String to, EmailType type) throws MessagingException, UnsupportedEncodingException {
        String key = createKey();
        emailVerifyRepository.findByEmailAndType(to, type).ifPresentOrElse(
                emailVerify -> {
                    emailVerify.setVerifyCode(key);
                    emailVerify.setAlreadyUsed(false);
                    emailVerifyRepository.save(emailVerify);
                },
                () -> {
                    EmailVerify emailVerify = EmailVerify.builder()
                            .email(to)
                            .verifyCode(key)
                            .type(type)
                            .alreadyUsed(false)
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

    public abstract void sendMessage(String to) throws MessagingException, UnsupportedEncodingException;

    protected static String createKey() {
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

}
