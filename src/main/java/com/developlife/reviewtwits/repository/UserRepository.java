package com.developlife.reviewtwits.repository;

import com.developlife.reviewtwits.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

/**
 * @author ghdic
 * @since 2023.02.19
 */
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByAccountId(String accountId);
    User findByAccountIdAndAccountPw(String accountId, String accountPw);
}
