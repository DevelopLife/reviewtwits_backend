package com.developlife.reviewtwits.repository.follow;

import com.developlife.reviewtwits.entity.Follow;
import com.developlife.reviewtwits.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long>, FollowCustomRepository {
    Optional<Follow> findByUserAndTargetUser(User user, User targetUser);
    boolean existsByUserAndTargetUser(User user, User targetUser);

    @Query("SELECT f.user FROM Follow f WHERE f.targetUser = :user")
    List<User> findFollowersOfUser(User user);

    @Query("SELECT f.targetUser FROM Follow f WHERE f.user = :user")
    List<User> findFollowingsOfUser(User user);
}
