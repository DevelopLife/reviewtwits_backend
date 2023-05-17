package com.developlife.reviewtwits.service;

import com.developlife.reviewtwits.entity.*;
import com.developlife.reviewtwits.exception.review.*;
import com.developlife.reviewtwits.exception.user.AccessDeniedException;
import com.developlife.reviewtwits.mapper.ReviewMapper;
import com.developlife.reviewtwits.message.request.review.SnsCommentWriteRequest;
import com.developlife.reviewtwits.message.request.review.SnsReviewChangeRequest;
import com.developlife.reviewtwits.message.request.review.SnsReviewWriteRequest;
import com.developlife.reviewtwits.message.response.review.CommentLikeResultResponse;
import com.developlife.reviewtwits.message.response.review.CommentResponse;
import com.developlife.reviewtwits.message.response.review.DetailReactionResponse;
import com.developlife.reviewtwits.message.response.review.ReviewScrapResultResponse;
import com.developlife.reviewtwits.message.response.sns.DetailSnsReviewResponse;
import com.developlife.reviewtwits.repository.*;
import com.developlife.reviewtwits.repository.review.ReviewRepository;
import com.developlife.reviewtwits.type.ReferenceType;
import com.developlife.reviewtwits.type.ReactionType;
import com.developlife.reviewtwits.type.ReviewStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;


/**
 * @author WhalesBob
 * @since 2023-03-31
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class SnsReviewService {

    private final ReviewMapper mapper;
    private final FileStoreService fileStoreService;

    private final ReviewRepository reviewRepository;
    private final CommentRepository commentRepository;
    private final ReactionRepository reactionRepository;
    private final ReviewScrapRepository reviewScrapRepository;
    private final CommentLikeRepository commentLikeRepository;

    private final SnsReviewUtils snsReviewUtils;

    @Transactional
    public DetailSnsReviewResponse saveSnsReview(SnsReviewWriteRequest writeRequest, User user){

        Review review = Review.builder()
                .user(user)
                .content(writeRequest.content())
                .productUrl(writeRequest.productURL())
                .score(Integer.parseInt(writeRequest.score()))
                .productName(writeRequest.productName())
                .build();

        reviewRepository.save(review);

        if(writeRequest.multipartImageFiles() != null) {
            fileStoreService.storeFiles(writeRequest.multipartImageFiles(), review.getReviewId(), ReferenceType.REVIEW);
            review.setReviewImageCount(writeRequest.multipartImageFiles().size());
        }

        snsReviewUtils.saveReviewImage(review);
        return mapper.toDetailSnsReviewResponse(review, new HashMap<>(), false);
    }

    @Transactional(readOnly = true)
    public List<DetailSnsReviewResponse> getSnsReviews(User user,Long reviewId, int size){
//        List<Review> pageReviews = findReviewsInPage(reviewId, size);
        Pageable pageable = PageRequest.of(0,size,Sort.by("reviewId").descending());

        List<DetailSnsReviewResponse> mappingReview = reviewRepository.findMappingReviewById(user, reviewId, pageable);
        if(mappingReview.size() == 0){
            throw new ReviewListEmptyException("");
        }
        return mappingReview;
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> getCommentInfo(long reviewId){
        if(!reviewRepository.existsById(reviewId)){
            throw new ReviewNotFoundException("댓글을 확인하려는 리뷰가 존재하지 않습니다.");
        }
        List<Comment> commentList = commentRepository.findByReview_ReviewId(reviewId);
        return mapper.toCommentResponseList(commentList);
    }

    @Transactional
    public CommentResponse saveComment(User user, long reviewId, SnsCommentWriteRequest request){
        long parentId = request.parentId();

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("댓글을 작성하려는 리뷰가 존재하지 않습니다."));

        Comment newComment = Comment.builder()
                .user(user)
                .review(review)
                .content(request.content())
                .build();

        Comment commentGroup = commentRepository.findById(parentId).orElse(newComment);
        newComment.setCommentGroup(commentGroup);
        commentRepository.save(newComment);

        int currentCommentCount = review.getCommentCount();
        review.setCommentCount(currentCommentCount + 1);

        reviewRepository.save(review);

        return mapper.toCommentResponse(newComment);
    }

    @Transactional
    public CommentResponse deleteComment(User user,long commentId) {
        Comment foundComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("지우고자 하는 댓글이 존재하지 않습니다."));

        if(!foundComment.getUser().equals(user)){
            throw new AccessDeniedException("해당 리뷰를 지울 권한이 없습니다.");
        }

        commentRepository.delete(foundComment);

        Review review = foundComment.getReview();
        int currentCommentCount = review.getCommentCount();
        review.setCommentCount(currentCommentCount - 1);
        reviewRepository.save(review);

        return mapper.toCommentResponse(foundComment);
    }

    @Transactional
    public CommentResponse changeComment(User user, long commentId, String content) {
        Comment foundComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("수정하고자 하는 댓글이 존재하지 않습니다."));

        if(!foundComment.getUser().equals(user)){
            throw new AccessDeniedException("해당 리뷰를 수정할 권한이 없습니다.");
        }

        foundComment.setContent(content);
        commentRepository.save(foundComment);
        return mapper.toCommentResponse(foundComment);
    }

    @Transactional
    public DetailReactionResponse reactionOnReview(User user, long reviewId, String inputReaction) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("공감을 누르려는 리뷰가 존재하지 않습니다."));

        Optional<Reaction> foundReaction = reactionRepository.findByReview_ReviewIdAndUser(reviewId, user);

        Reaction toUpdateReaction;
        if(foundReaction.isPresent()){
            toUpdateReaction = foundReaction.get();
            if(toUpdateReaction.getReactionType().equals(ReactionType.valueOf(inputReaction))){
                reactionRepository.delete(toUpdateReaction);
                modifyReactionCountOnReview(review,-1);
                return mapper.toDetailReactionResponse(toUpdateReaction);
            }

            toUpdateReaction.setReactionType(ReactionType.valueOf(inputReaction));
        }else{
            toUpdateReaction = Reaction.builder()
                    .reactionType(ReactionType.valueOf(inputReaction))
                    .review(review)
                    .user(user)
                    .build();

            modifyReactionCountOnReview(review,1);
        }

        reactionRepository.save(toUpdateReaction);

        return mapper.toDetailReactionResponse(toUpdateReaction);
    }

//    @Transactional
//    public DetailReactionResponse deleteReactionOnReview(User user, long reviewId) {
//        Review review = reviewRepository.findById(reviewId)
//                .orElseThrow(() -> new ReviewNotFoundException("삭제하려는 리액션의 리뷰가 존재하지 않습니다."));
//
//        Reaction reaction = reactionRepository.findByReview_ReviewIdAndUser(reviewId, user)
//                .orElseThrow(() -> new ReactionNotFoundException("삭제하려는 리액션이 존재하지 않습니다."));
//
//        reactionRepository.delete(reaction);
//
//        modifyReactionCountOnReview(review, -1);
//
//        return mapper.toDetailReactionResponse(reaction);
//    }


    private void modifyReactionCountOnReview(Review review, int count) {
        int reactionCount = review.getReactionCount();
        review.setReactionCount(reactionCount + count);
        reviewRepository.save(review);
    }


    @Transactional
    public DetailSnsReviewResponse deleteSnsReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("삭제하려는 리뷰가 존재하지 않습니다."));

        review.setStatus(ReviewStatus.DELETED);
        reviewRepository.save(review);

        review.setReviewImageUuidList(new ArrayList<>());
        return mapper.toDetailSnsReviewResponse(review, new HashMap<>(), false);
    }

    private List<Review> findReviewsInPage(Long reviewId, int size){
        Pageable pageable = PageRequest.of(0,size,Sort.by("reviewId").descending());
        if(reviewId == null){
            return reviewRepository.findAll(pageable).getContent();
        }
        Page<Review> reviewInPage = reviewRepository.findByReviewIdLessThan(reviewId, pageable);
        return reviewInPage.getContent();
    }

    @Transactional(readOnly = true)
    public void checkReviewCanEdit(User user, Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("입력된 리뷰아이디로 등록된 리뷰가 존재하지 않습니다."));

        if(!review.getUser().equals(user)){
            throw new CannotHandleReviewException("해당 유저의 권한으로 이 리뷰를 수정 또는 삭제할 수 없습니다.");
        }
    }

    @Transactional
    public DetailSnsReviewResponse changeSnsReview(Long reviewId, SnsReviewChangeRequest changeRequest) {
        Review review = reviewRepository.findById(reviewId).get();
        if(changeRequest.content() != null){
            review.setContent(changeRequest.content());
        }
        if(changeRequest.score() != null){
            review.setScore(Integer.parseInt(changeRequest.score()));
        }

        if(changeRequest.multipartImageFiles() != null && !changeRequest.multipartImageFiles().isEmpty()){
            fileStoreService.storeFiles(changeRequest.multipartImageFiles(),review.getReviewId(), ReferenceType.REVIEW);
            int currentImageCount = review.getReviewImageUuidList().size() + changeRequest.multipartImageFiles().size();
            review.setReviewImageCount(currentImageCount);
        }

        if(changeRequest.deleteFileList() != null && !changeRequest.deleteFileList().isEmpty()){
            fileStoreService.checkDeleteFile(changeRequest.deleteFileList());
            int currentImageCount = review.getReviewImageUuidList().size() - changeRequest.deleteFileList().size();
            review.setReviewImageCount(currentImageCount);
        }

        reviewRepository.save(review);
        snsReviewUtils.saveReviewImage(review);
        return mapper.toDetailSnsReviewResponse(review, new HashMap<>(), false);
    }

    @Transactional
    public ReviewScrapResultResponse addReviewScrap(User user, long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("스크랩하려는 리뷰 아이디가 존재하지 않습니다."));

        if(reviewScrapRepository.existsByReviewAndUser(review,user)){
            throw new ReviewScrapConflictException("이미 등록된 리뷰 스크랩입니다.");
        }

        ReviewScrap reviewScrap = ReviewScrap.builder()
                .review(review)
                .user(user)
                .build();

        reviewScrapRepository.save(reviewScrap);

        return mapper.toReviewScrapResultResponse(reviewScrap);
    }

    @Transactional
    public ReviewScrapResultResponse deleteReviewScrap(User user, long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("삭제하려는 리뷰가 존재하지 않습니다."));

        ReviewScrap reviewScrap = reviewScrapRepository.findByReviewAndUser(review, user)
                .orElseThrow(() -> new ReviewScrapConflictException("등록되지 않은 리뷰 스크랩입니다."));

        reviewScrapRepository.delete(reviewScrap);

        return mapper.toReviewScrapResultResponse(reviewScrap);
    }

    @Transactional(readOnly = true)
    public List<DetailSnsReviewResponse> getReviewsInUserScrap(User user) {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("reviewId").descending());
        return reviewRepository.findMappingReviewScrappedByUser(user, pageable);
    }

    @Transactional
    public CommentLikeResultResponse addLikeOnComment(User user, Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("좋아요를 입력할 comment 가 존재하지 않습니다."));

        if(commentLikeRepository.existsByUserAndComment(user, comment)){
            throw new CommentLikeAlreadyProcessedException("이미 해당 댓글에 좋아요를 누르셨습니다.");
        }
        CommentLike commentLike = CommentLike.builder()
                .comment(comment)
                .user(user)
                .build();

        commentLikeRepository.save(commentLike);

        saveLikeCount(comment, 1);

        return mapper.toCommentLikeResultResponse(commentLike);
    }

    @Transactional
    public CommentLikeResultResponse deleteLikeOnComment(User user, Long commentId){
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("좋아요를 취소할 comment 가 존재하지 않습니다."));

        CommentLike commentLike = commentLikeRepository.findByUserAndComment(user, comment)
                .orElseThrow(() -> new CommentLikeAlreadyProcessedException("해당 댓글에 좋아요를 누르지 않으셨거나, 이미 취소한 좋아요입니다."));

        commentLikeRepository.delete(commentLike);
        saveLikeCount(comment, -1);

        return mapper.toCommentLikeResultResponse(commentLike);
    }

    private void saveLikeCount(Comment comment, int count){
        int commentLike = comment.getCommentLike();
        comment.setCommentLike(commentLike + count);
        commentRepository.save(comment);
    }

    public DetailSnsReviewResponse getOneSnsReview(User user, long reviewId) {

        DetailSnsReviewResponse mappingReview = reviewRepository.findOneMappingReviewById(user, reviewId);
        if(mappingReview == null){
            throw new ReviewNotFoundException("찾으려는 리뷰가 존재하지 않습니다.");
        }
        return mappingReview;
    }

}
