package com.developlife.reviewtwits.service;

import com.developlife.reviewtwits.entity.Follow;
import com.developlife.reviewtwits.entity.User;
import com.developlife.reviewtwits.exception.sns.FollowAlreadyExistsException;
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

        Optional<User> foundTargetUser = userRepository.findByAccountId(targetUserAccountId);
        if(foundTargetUser.isEmpty()){
            throw new UserIdNotFoundException("요청한 팔로우 계정이 존재하지 않습니다.");
        }

        User targetUser = foundTargetUser.get();
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
}