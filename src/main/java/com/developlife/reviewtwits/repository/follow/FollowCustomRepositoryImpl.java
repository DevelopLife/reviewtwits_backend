package com.developlife.reviewtwits.repository.follow;

import com.developlife.reviewtwits.entity.User;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.developlife.reviewtwits.entity.QFollow.follow;
import static com.developlife.reviewtwits.entity.QUser.user;

/**
 * @author ghdic
 * @since 2023/04/09
 */
@Repository
public class FollowCustomRepositoryImpl implements FollowCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public FollowCustomRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public List<User> recommendFollow(long userId, int limit) {
        return jpaQueryFactory.selectFrom(user)
                .where(
                    JPAExpressions.selectFrom(follow)
                        .where(follow.targetUser.eq(user).and(follow.user.userId.eq(userId)))
                        .notExists()
                )
                .fetch();
    }
}
/*
SELECT user_id
FROM users
WHERE NOT EXISTS (
  SELECT 1
  FROM followers
  WHERE followers.followed_id = users.user_id AND followers.follower_id = <current_user_id>
)
 */