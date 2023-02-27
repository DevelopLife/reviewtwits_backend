package com.developlife.reviewtwits.repository;

import com.developlife.reviewtwits.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author ghdic
 * @since 2023/02/27
 */
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByAccountId(String accountId);
    RefreshToken save(RefreshToken refreshToken);
}
