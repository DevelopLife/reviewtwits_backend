package com.developlife.reviewtwits.repository;

import com.developlife.reviewtwits.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Ref;
import java.util.Optional;

/**
 * @author ghdic
 * @since 2023/02/27
 */
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByAccountId(String accountId);

    Optional<RefreshToken> findByToken(String token);
    RefreshToken save(RefreshToken refreshToken);

    void delete(RefreshToken refreshToken);
}
