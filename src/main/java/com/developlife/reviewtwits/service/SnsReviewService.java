package com.developlife.reviewtwits.service;

import com.developlife.reviewtwits.entity.*;
import com.developlife.reviewtwits.exception.review.*;
import com.developlife.reviewtwits.exception.user.UnAuthorizedException;
import com.developlife.reviewtwits.mapper.ReviewMapper;
import com.developlife.reviewtwits.message.request.review.SnsCommentWriteRequest;
import com.developlife.reviewtwits.message.request.review.SnsReviewChangeRequest;
import com.developlife.reviewtwits.message.request.review.SnsReviewWriteRequest;
import com.developlife.reviewtwits.message.response.review.CommentResponse;
import com.developlife.reviewtwits.message.response.sns.DetailSnsReviewResponse;
import com.developlife.reviewtwits.repository.CommentRepository;
import com.developlife.reviewtwits.repository.ReactionRepository;
import com.developlife.reviewtwits.repository.ReviewRepository;
import com.developlife.reviewtwits.repository.ReviewScrapRepository;
import com.developlife.reviewtwits.type.FileReferenceType;
import com.developlife.reviewtwits.type.ReactionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


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
    private final SnsReviewUtils snsReviewUtils;

    @Transactional
    public void saveSnsReview(SnsReviewWriteRequest writeRequest, User user){

        Review review = Review.builder()
                .user(user)
                .content(writeRequest.content())
                .productUrl(writeRequest.productURL())
                .score(Integer.parseInt(writeRequest.score()))
                .productName(writeRequest.productName())
                .build();

        reviewRepository.save(review);

        if(writeRequest.multipartImageFiles() != null) {
            fileStoreService.storeFiles(writeRequest.multipartImageFiles(), review.getReviewId(), FileReferenceType.REVIEW);
        }
    }

    @Transactional(readOnly = true)
    public List<DetailSnsReviewResponse> getSnsReviews(User user,Long reviewId, int size){
        List<Review> pageReviews = findReviewsInPage(reviewId, size);
        return snsReviewUtils.processAndExportReviewData(pageReviews, user);
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
    public void saveComment(User user, long reviewId, SnsCommentWriteRequest request){
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
    }

    @Transactional
    public void deleteComment(User user,long commentId) {
        Comment foundComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("지우고자 하는 댓글이 존재하지 않습니다."));

        if(!foundComment.getUser().equals(user)){
            throw new UnAuthorizedException("해당 리뷰를 지울 권한이 없습니다.");
        }

        commentRepository.delete(foundComment);

        Review review = foundComment.getReview();
        int currentCommentCount = review.getCommentCount();
        review.setCommentCount(currentCommentCount - 1);
        reviewRepository.save(review);
    }

    @Transactional
    public String changeComment(User user, long commentId, String content) {
        Comment foundComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("수정하고자 하는 댓글이 존재하지 않습니다."));

        if(!foundComment.getUser().equals(user)){
            throw new UnAuthorizedException("해당 리뷰를 수정할 권한이 없습니다.");
        }

        foundComment.setContent(content);
        commentRepository.save(foundComment);
        return content;
    }

    @Transactional
    public void addReactionOnReview(User user, long reviewId, String reaction) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("공감을 누르려는 리뷰가 존재하지 않습니다."));

        reactionRepository.save(Reaction.builder()
                .reactionType(ReactionType.valueOf(reaction))
                .review(review)
                .user(user)
                .build());
    }

    @Transactional
    public void deleteReactionOnReview(User user, long reviewId) {
        Reaction reaction = reactionRepository.findByReview_ReviewIdAndUser(reviewId, user)
                .orElseThrow(() -> new ReactionNotFoundException("삭제하려는 리액션이 존재하지 않습니다."));

        reactionRepository.delete(reaction);
    }

    @Transactional
    public void deleteSnsReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("삭제하려는 리뷰가 존재하지 않습니다."));

        review.setExist(false);
        reviewRepository.save(review);
    }

    @Transactional(readOnly = true)
    public List<Review> findReviewsInPage(Long reviewId, int size){
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
    public void changeSnsReview(Long reviewId, SnsReviewChangeRequest changeRequest) {
        Review review = reviewRepository.findById(reviewId).get();
        if(changeRequest.content() != null){
            review.setContent(changeRequest.content());
        }
        if(changeRequest.score() != null){
            review.setScore(Integer.parseInt(changeRequest.score()));
        }
        reviewRepository.save(review);

        if(changeRequest.multipartImageFiles() != null && !changeRequest.multipartImageFiles().isEmpty()){
            fileStoreService.storeFiles(changeRequest.multipartImageFiles(),review.getReviewId(),FileReferenceType.REVIEW);
        }

        if(changeRequest.deleteFileList() != null && !changeRequest.deleteFileList().isEmpty()){
            fileStoreService.checkDeleteFile(changeRequest.deleteFileList());
        }
    }

    @Transactional
    public void addReviewScrap(User user, long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("스크랩하려는 리뷰 아이디가 존재하지 않습니다."));

        ReviewScrap reviewScrap = ReviewScrap.builder()
                .review(review)
                .user(user)
                .build();

        reviewScrapRepository.save(reviewScrap);
    }

    @Transactional
    public void deleteReviewScrap(User user, long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("삭제하려는 리뷰가 존재하지 않습니다."));

        ReviewScrap reviewScrap = reviewScrapRepository.findByReviewAndUser(review, user)
                .orElseThrow(() -> new ReviewScrapNotAddedException("등록되지 않은 리뷰 스크랩입니다."));

        reviewScrapRepository.delete(reviewScrap);
    }

    @Transactional(readOnly = true)
    public List<DetailSnsReviewResponse> getReviewsInUserScrap(User user) {
        List<Review> reviewOnUserScrap = reviewScrapRepository.findReviewByUser(user);

        return snsReviewUtils.processAndExportReviewData(reviewOnUserScrap, user);
    }
}
