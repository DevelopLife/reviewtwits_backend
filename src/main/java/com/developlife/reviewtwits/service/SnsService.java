package com.developlife.reviewtwits.service;

import com.developlife.reviewtwits.entity.Follow;
import com.developlife.reviewtwits.entity.User;
import com.developlife.reviewtwits.exception.sns.FollowAlreadyExistsException;
import com.developlife.reviewtwits.exception.sns.UnfollowAlreadyDoneException;
import com.developlife.reviewtwits.exception.user.UserIdNotFoundException;
import com.developlife.reviewtwits.repository.FollowRepository;
import com.developlife.reviewtwits.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author WhalesBob
 * @since 2023-03-20
 */
@Service
@RequiredArgsConstructor
public class SnsService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    public void followProcess(User user, String targetUserAccountId){
        User targetUser = getTargetUser(targetUserAccountId);
        if(followRepository.existsByUserAndTargetUser(user, targetUser)){
            throw new FollowAlreadyExistsException("이미 수행된 팔로우 요청입니다");
        }

        Follow newFollow = Follow.builder().user(user).targetUser(targetUser).build();

        Optional<Follow> foundBackFollow = followRepository.findByUserAndTargetUser(targetUser, user);
        if(foundBackFollow.isPresent()){
            Follow backFollow = foundBackFollow.get();
            backFollow.setFollowBackFlag(true);
            newFollow.setFollowBackFlag(true);
            followRepository.saveAll(List.of(newFollow, backFollow));
        }else{
            followRepository.save(newFollow);
        }
    }

    public void unfollowProcess(User user, String targetUserAccountId){
        User targetUser = getTargetUser(targetUserAccountId);
        Optional<Follow> foundFollow = followRepository.findByUserAndTargetUser(user, targetUser);
        if(foundFollow.isEmpty()){
            throw new UnfollowAlreadyDoneException("이미 팔로우되어 있지 않은 상태입니다.");
        }

        Optional<Follow> foundBackFollow = followRepository.findByUserAndTargetUser(targetUser, user);
        foundBackFollow.ifPresent(follow -> follow.setFollowBackFlag(false));
        followRepository.delete(foundFollow.get());
    }

    private User getTargetUser(String targetUserAccountId) {
        Optional<User> foundTargetUser = userRepository.findByAccountId(targetUserAccountId);
        if(foundTargetUser.isEmpty()){
            throw new UserIdNotFoundException("요청한 팔로우 계정이 존재하지 않습니다.");
        }
        return foundTargetUser.get();
    }
}