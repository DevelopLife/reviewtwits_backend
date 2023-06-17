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
import java.time.LocalDateTime;
import java.util.UUID;

public abstract class EmailSender {

    protected final JavaMailSender emailSender;
    protected final EmailVerifyRepository emailVerifyRepository;

    protected EmailSender(JavaMailSender emailSender, EmailVerifyRepository emailVerifyRepository) {
        this.emailSender = emailSender;
        this.emailVerifyRepository = emailVerifyRepository;
    }

    protected abstract MimeMessage createMessage(String email, String element) throws MessagingException, UnsupportedEncodingException;

    @Transactional
    public void sendMessage(String to, EmailType type) throws MessagingException, UnsupportedEncodingException {

        String key = storageVerifyInfo(to, type);

        MimeMessage message = createMessage(to, key);
        try{
            emailSender.send(message);
        }catch(MailException es){
            throw new MailSendException("메일 발송 실패");
        }
    }

    @Transactional
    public String storageVerifyInfo(String to, EmailType type) {
        String key = createUniqueKey();
        emailVerifyRepository.findByEmailAndType(to, type).ifPresentOrElse(
                emailVerify -> {
                    emailVerify.setVerifyCode(key);
                    emailVerify.setAlreadyUsed(false);
                    emailVerify.setVerifyDate(LocalDateTime.now());
                    emailVerifyRepository.save(emailVerify);
                },
                () -> {
                    EmailVerify emailVerify = EmailVerify.builder()
                        .email(to)
                        .verifyCode(key)
                        .verifyDate(LocalDateTime.now())
                        .type(type)
                        .alreadyUsed(false)
                        .build();
                    emailVerifyRepository.save(emailVerify);
                }
        );
        return key;
    }

    private String createUniqueKey() {
        String key;
        do {
            key = UUID.randomUUID().toString().substring(0, 8);
        } while(emailVerifyRepository.findByVerifyCode(key).isPresent());
        return key;
    }

    public abstract void sendMessage(String to) throws MessagingException, UnsupportedEncodingException;

}
