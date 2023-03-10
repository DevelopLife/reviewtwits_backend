package com.developlife.reviewtwits.repository;

import com.developlife.reviewtwits.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author ghdic
 * @since 2023.02.19
 */
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByAccountId(String accountId);
    User findByAccountIdAndAccountPw(String accountId, String accountPw);

    List<User> findByPhoneNumberAndBirthDate(String phoneNumber, Date birthDate);

    Optional<User> findByAccountIdAndPhoneNumberAndBirthDate(String accountId, String phoneNumber, Date birthDate);
}
