package com.developlife.reviewtwits.repository.follow;

import com.developlife.reviewtwits.entity.User;
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
    /*
   // 최근에 만들어진 계정 중에 팔로우 하지 않은 계정을 추천
    SELECT user_id From User

     */
    @Override
    public List<User> recommendFollow(long userId, int limit) {
        return jpaQueryFactory.selectFrom(user)
                .where(
                    JPAExpressions.selectFrom(follow)
                        .where(follow.targetUser.eq(user).and(follow.user.userId.eq(userId)))
                        .notExists()
                            .and(user.userId.ne(userId))
                )
                .orderBy(user.createdDate.desc())
                .limit(limit)
                .fetch();
    }

    @Override
    public List<User> findFollowersOfUser(User inputUser, int limit, Long userId) {
        if(userId == null){
            userId = Long.MAX_VALUE;
        }

        return jpaQueryFactory.select(follow.user).from(follow)
                .where(follow.targetUser.eq(inputUser).and(follow.user.userId.lt(userId)))
                .orderBy(follow.user.userId.desc())
                .limit(limit)
                .fetch();
    }

    @Override
    public List<User> findFollowingsOfUser(User inputUser, int limit, Long userId) {
        if(userId == null){
            userId = Long.MAX_VALUE;
        }

        return jpaQueryFactory.select(follow.targetUser).from(follow)
                .where(follow.user.eq(inputUser).and(follow.targetUser.userId.lt(userId)))
                .orderBy(follow.targetUser.userId.desc())
                .limit(limit)
                .fetch();
    }
}