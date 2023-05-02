package com.developlife.reviewtwits.repository.follow;

import com.developlife.reviewtwits.entity.User;

import java.util.List;

public interface FollowCustomRepository {
    List<User> recommendFollow(long userId, int limit);
    List<User> findFollowersOfUser(User inputUser, int limit, Long userId);
    List<User> findFollowingsOfUser(User inputUser, int limit, Long userId);
}
