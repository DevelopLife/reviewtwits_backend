package com.developlife.reviewtwits.repository;

import com.developlife.reviewtwits.entity.EmailVerify;
import com.developlife.reviewtwits.type.EmailType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailVerifyRepository extends JpaRepository<EmailVerify, Long> {
    Optional<EmailVerify> findByEmail(String email);
    EmailVerify save(EmailVerify emailVerify);

    Optional<EmailVerify> findByEmailAndType(String email, EmailType type);

    Optional<EmailVerify> findByVerifyCode(String verifyCode);
}
