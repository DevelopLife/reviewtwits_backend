package com.developlife.reviewtwits.repository;

import com.developlife.reviewtwits.entity.User;
import com.developlife.reviewtwits.type.JwtProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author ghdic
 * @since 2023.02.19
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByAccountId(String accountId);
    User findByAccountIdAndAccountPw(String accountId, String accountPw);

    List<User> findByPhoneNumberAndBirthDate(String phoneNumber, Date birthDate);

    Optional<User> findByAccountIdAndPhoneNumberAndBirthDate(String accountId, String phoneNumber, Date birthDate);

    Optional<User> findByAccountIdOrPhoneNumber(String accountId, String phoneNumber);

    Optional<User> findByUuid(String uuid);

    Optional<User> findByUuidAndProvider(String uuid, JwtProvider provider);

    Optional<User> findByNickname(String nickname);

    boolean existsByPhoneNumber(String phoneNumber);

}
