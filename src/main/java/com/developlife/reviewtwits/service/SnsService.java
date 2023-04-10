package com.developlife.reviewtwits.service;

import com.developlife.reviewtwits.entity.Follow;
import com.developlife.reviewtwits.entity.ItemDetail;
import com.developlife.reviewtwits.entity.Review;
import com.developlife.reviewtwits.entity.User;
import com.developlife.reviewtwits.exception.sns.FollowAlreadyExistsException;
import com.developlife.reviewtwits.exception.sns.UnfollowAlreadyDoneException;
import com.developlife.reviewtwits.exception.user.UserIdNotFoundException;
import com.developlife.reviewtwits.mapper.SnsMapper;
import com.developlife.reviewtwits.mapper.UserMapper;
import com.developlife.reviewtwits.message.response.sns.DetailSnsReviewResponse;
import com.developlife.reviewtwits.message.response.sns.ItemResponse;
import com.developlife.reviewtwits.message.response.sns.SearchAllResponse;
import com.developlife.reviewtwits.message.response.user.UserInfoResponse;
import com.developlife.reviewtwits.repository.*;
import com.developlife.reviewtwits.repository.follow.FollowRepository;
import com.developlife.reviewtwits.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final ReviewRepository reviewRepository;
    private final ReactionRepository reactionRepository;
    private final ItemDetailRepository itemDetailRepository;
    private final ReviewScrapRepository reviewScrapRepository;
    private final UserService userService;
    private final SnsReviewService snsReviewService;
    private final SnsReviewUtils snsReviewUtils;
    private final UserMapper userMapper;
    private final SnsMapper snsMapper;

    @Transactional
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

    @Transactional
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

    @Transactional(readOnly = true)
    private User getTargetUser(String targetUserAccountId) {
        Optional<User> foundTargetUser = userRepository.findByAccountId(targetUserAccountId);
        if(foundTargetUser.isEmpty()){
            throw new UserIdNotFoundException("요청한 팔로우 계정이 존재하지 않습니다.");
        }
        return foundTargetUser.get();
    }

    @Transactional(readOnly = true)
    public List<UserInfoResponse> getFollowerList(String accountId){
        User targetUser = getTargetUser(accountId);
        List<User> followersList = followRepository.findUsersByTargetUser(targetUser);
        return getUserInfoResponses(followersList);
    }

    @Transactional(readOnly = true)
    public List<UserInfoResponse> getFollowingList(String accountId){
        User targetUser = getTargetUser(accountId);
        List<User> followingsList = followRepository.findTargetUsersByUser(targetUser);
        return getUserInfoResponses(followingsList);
    }

    @Transactional(readOnly = true)
    public List<UserInfoResponse> getUserInfoResponses(List<User> followersList) {
        for(User user : followersList){
            userService.setProfileImage(user);
        }
        return userMapper.toUserInfoResponseList(followersList);
    }

    @Transactional(readOnly = true)
    public SearchAllResponse searchAll(String searchKey, User user) {
        List<ItemDetail> itemDetailList = itemDetailRepository.findByRelatedProduct_NameLikeOrDetailInfoLike(searchKey, PageRequest.of(0, 3));

        List<Review> reviewList = reviewRepository.findByProductNameLikeOrContentLike(searchKey, PageRequest.of(0, 10));
        List<DetailSnsReviewResponse> snsReviewResponseList = snsReviewUtils.processAndExportReviewData(reviewList, user);

        return snsMapper.toSearchAllResponse(itemDetailList, snsReviewResponseList);
    }

    // TODO: 임시적으로 최근 생성된 3개를 반환하도록,, 추천 알고리즘 구현 필요
    @Transactional(readOnly = true)
    public List<ItemResponse> recommendProduct() {
        List<ItemDetail> itemDetailList = itemDetailRepository.findAllByOrderByCreatedDateDesc(PageRequest.of(0, 3));
        return itemDetailList.stream().map(itemDetail -> snsMapper.toItemResponse(itemDetail)).toList();
    }

    // TODO: 임시적으로 최근 생성된 5개를 반환하도록,, 추천 알고리즘 구현 필요
    public List<UserInfoResponse> suggestFollowers(User user) {
        List<User> userList = followRepository.recommendFollow(user.getUserId(), 5);
        return userMapper.toUserInfoResponseList(userList);
    }
}