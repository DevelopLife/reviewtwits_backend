package com.developlife.reviewtwits.service;

import com.developlife.reviewtwits.entity.Follow;
import com.developlife.reviewtwits.entity.ItemDetail;
import com.developlife.reviewtwits.entity.Review;
import com.developlife.reviewtwits.entity.User;
import com.developlife.reviewtwits.exception.sns.FollowAlreadyExistsException;
import com.developlife.reviewtwits.exception.sns.UnfollowAlreadyDoneException;
import com.developlife.reviewtwits.exception.user.UserIdNotFoundException;
import com.developlife.reviewtwits.mapper.FollowMapper;
import com.developlife.reviewtwits.mapper.ReviewMapper;
import com.developlife.reviewtwits.mapper.SnsMapper;
import com.developlife.reviewtwits.mapper.UserMapper;
import com.developlife.reviewtwits.message.response.sns.*;
import com.developlife.reviewtwits.message.response.user.UserInfoResponse;
import com.developlife.reviewtwits.repository.*;
import com.developlife.reviewtwits.repository.follow.FollowRepository;
import com.developlife.reviewtwits.repository.review.ReviewRepository;
import com.developlife.reviewtwits.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
    private final ReviewMapper reviewMapper;
    private final FollowMapper followMapper;

    @Transactional
    public FollowResultResponse followProcess(User user, String targetUserAccountId){
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

        return followMapper.toFollowResultResponse(newFollow);
    }

    @Transactional
    public FollowResultResponse unfollowProcess(User user, String targetUserAccountId){
        User targetUser = getTargetUser(targetUserAccountId);
        Optional<Follow> foundFollow = followRepository.findByUserAndTargetUser(user, targetUser);
        if(foundFollow.isEmpty()){
            throw new UnfollowAlreadyDoneException("이미 팔로우되어 있지 않은 상태입니다.");
        }

        Optional<Follow> foundBackFollow = followRepository.findByUserAndTargetUser(targetUser, user);
        foundBackFollow.ifPresent(follow -> follow.setFollowBackFlag(false));
        followRepository.delete(foundFollow.get());

        return followMapper.toFollowResultResponse(foundFollow.get());
    }

    @Transactional(readOnly = true)
    public User getTargetUser(String targetUserAccountId) {
        Optional<User> foundTargetUser = userRepository.findByAccountId(targetUserAccountId);
        if(foundTargetUser.isEmpty()){
            throw new UserIdNotFoundException("요청한 팔로우 계정이 존재하지 않습니다.");
        }
        return foundTargetUser.get();
    }

    @Transactional(readOnly = true)
    public List<UserInfoResponse> getFollowerList(String accountId){
        User targetUser = getTargetUser(accountId);
        List<User> followersList = followRepository.findFollowersOfUser(targetUser);
        return getUserInfoResponses(followersList);
    }

    @Transactional(readOnly = true)
    public List<UserInfoResponse> getFollowingList(String accountId){
        User targetUser = getTargetUser(accountId);
        List<User> followingsList = followRepository.findFollowingsOfUser(targetUser);
        return getUserInfoResponses(followingsList);
    }

    @Transactional(readOnly = true)
    public List<UserInfoResponse> getUserInfoResponses(List<User> followersList) {
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
    @Transactional(readOnly = true)
    public List<UserInfoResponse> suggestFollowers(User user) {
        List<User> userList = followRepository.recommendFollow(user.getUserId(), 5);
        return userMapper.toUserInfoResponseList(userList);
    }

    @Transactional(readOnly = true)
    public UserInfoResponse findUserProfile(String nickname) {

        User user = userRepository.findByNickname(nickname)
                .orElseThrow(() -> new UsernameNotFoundException("해당 유저 닉네임으로 된 계정이 존재하지 않습니다."));

        List<User> followers = followRepository.findFollowersOfUser(user);
        List<User> followings = followRepository.findFollowingsOfUser(user);
        List<Review> reviews = reviewRepository.findReviewsByUser(user);

        return userMapper.toUserInfoResponse(user,followers.size(), followings.size(),reviews.size());
    }

    @Transactional(readOnly = true)
    public List<SnsReviewResponse> findReviewsOfUser(String nickname,Long reviewId, int size) {
        User user = userRepository.findByNickname(nickname)
                .orElseThrow(() -> new UsernameNotFoundException("해당 유저 닉네임으로 된 계정이 존재하지 않습니다."));

        List<Review> reviews = findReviewsByUserInPage(user,reviewId,size);
        for(Review review : reviews){
            snsReviewUtils.saveReviewImage(review);
        }
        return reviewMapper.toSnsReviewResponseList(reviews);
    }

    private List<Review> findReviewsByUserInPage(User user,Long reviewId, int size){
        Pageable pageable = PageRequest.of(0,size, Sort.by("reviewId").descending());
        if(reviewId == null){
            return reviewRepository.findReviewsByUser(user,pageable).getContent();
        }
        Page<Review> reviewInPage = reviewRepository.findByReviewIdLessThanAndUser(reviewId,user,pageable);
        return reviewInPage.getContent();
    }
}