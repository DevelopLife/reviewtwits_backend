package com.developlife.reviewtwits.repository;

import com.developlife.reviewtwits.entity.Follow;
import com.developlife.reviewtwits.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {
    Optional<Follow> findByUserAndTargetUser(User user, User targetUser);
    boolean existsByUserAndTargetUser(User user, User targetUser);
}
