package com.developlife.reviewtwits.service;

import com.developlife.reviewtwits.entity.Reaction;
import com.developlife.reviewtwits.entity.Review;
import com.developlife.reviewtwits.entity.ReviewScrap;
import com.developlife.reviewtwits.entity.User;
import com.developlife.reviewtwits.mapper.ReviewMapper;
import com.developlife.reviewtwits.message.response.review.ReactionResponse;
import com.developlife.reviewtwits.message.response.sns.DetailSnsReviewResponse;
import com.developlife.reviewtwits.repository.ReactionRepository;
import com.developlife.reviewtwits.repository.ReviewRepository;
import com.developlife.reviewtwits.repository.ReviewScrapRepository;
import com.developlife.reviewtwits.type.ReferenceType;
import com.developlife.reviewtwits.type.ReactionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author ghdic
 * @since 2023/04/07
 */
@RequiredArgsConstructor
@Component
public class SnsReviewUtils {

    private final ReactionRepository reactionRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    private final ReviewScrapRepository reviewScrapRepository;
    private final FileStoreService fileStoreService;

    @Transactional(readOnly = true)
    public List<DetailSnsReviewResponse> processAndExportReviewData(List<Review> pageReviews, User user) {
        List<DetailSnsReviewResponse> snsResponse = new ArrayList<>();
        for(Review review : pageReviews){
            saveReviewImage(review);
            List<Reaction> reactionList = reactionRepository.findByReview(review);
            Map<String, ReactionResponse> collectedReactionResponse = ReactionType.classifyReactionResponses(user, reactionList);

            Optional<ReviewScrap> reviewScrap = reviewScrapRepository.findByReviewAndUser(review, user);
            snsResponse.add(reviewMapper.toDetailSnsReviewResponse(review, collectedReactionResponse,reviewScrap.isPresent()));
        }
        return snsResponse;
    }

    public void saveReviewImage(Review review){
        review.setReviewImageNameList(fileStoreService.bringFileNameList(ReferenceType.REVIEW, review.getReviewId()));
    }
}
