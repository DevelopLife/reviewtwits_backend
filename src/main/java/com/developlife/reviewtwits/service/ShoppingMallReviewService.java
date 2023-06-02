package com.developlife.reviewtwits.service;

import com.developlife.reviewtwits.entity.*;
import com.developlife.reviewtwits.exception.project.ProductUrlInvalidException;
import com.developlife.reviewtwits.exception.project.ProjectNotFoundException;
import com.developlife.reviewtwits.exception.review.CannotHandleReviewException;
import com.developlife.reviewtwits.exception.review.ReviewNotFoundException;
import com.developlife.reviewtwits.mapper.ReviewMapper;
import com.developlife.reviewtwits.message.request.review.ShoppingMallReviewChangeRequest;
import com.developlife.reviewtwits.message.request.review.ShoppingMallReviewWriteRequest;
import com.developlife.reviewtwits.message.response.review.DetailReactionResponse;
import com.developlife.reviewtwits.message.response.review.DetailShoppingMallReviewResponse;
import com.developlife.reviewtwits.message.response.review.ShoppingMallReviewProductResponse;
import com.developlife.reviewtwits.repository.ProductRepository;
import com.developlife.reviewtwits.repository.ReactionRepository;
import com.developlife.reviewtwits.repository.review.ReviewRepository;
import com.developlife.reviewtwits.type.ReferenceType;
import com.developlife.reviewtwits.type.review.ReviewStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * @author WhalesBob
 * @since 2023-03-13
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class ShoppingMallReviewService {

    private final ReviewMapper mapper;
    private final FileStoreService fileStoreService;
    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final ReactionRepository reactionRepository;

    public DetailShoppingMallReviewResponse saveShoppingMallReview(ShoppingMallReviewWriteRequest writeRequest, User user) {

        Project project = findProject(writeRequest.productURL());

        Review review = Review.builder()
                .user(user)
                .project(project)
                .content(writeRequest.content())
                .productUrl(writeRequest.productURL())
                .score(Integer.parseInt(writeRequest.score()))
                .status(ReviewStatus.PENDING)
                .build();

        Review savedReview = reviewRepository.save(review);
        List<String> fileNames;

        if(writeRequest.multipartImageFiles() != null) {
            List<FileInfo> fileInfoList = fileStoreService.storeFiles(writeRequest.multipartImageFiles(), review.getReviewId(), ReferenceType.REVIEW);
            fileNames = fileStoreService.getFileNameList(fileInfoList);
            savedReview.setReviewImageUuidList(fileNames);
        }

        return mapper.mapReviewToDetailReviewResponse(savedReview);
    }

    public ShoppingMallReviewProductResponse findShoppingMallReviewTotalInfo(String productURL){
        if(!productRepository.existsProductByProductUrl(productURL)){
            return null;
        }

        List<Review> reviews = reviewRepository.findReviewsByProductUrlAndProjectIsNotNull(productURL);

        if(reviews.isEmpty()){
            return ShoppingMallReviewProductResponse.builder().totalReviewCount(0).build();
        }

        int starScoreSum = 0;
        int[] starScoreArray = new int[5];
        int recentReviewCount = 0;
        int totalReviewCount = 0;
        for(Review review : reviews){
            if(review.getStatus() != ReviewStatus.DELETED){
                totalReviewCount++;
                starScoreArray[review.getScore()-1]++;
                starScoreSum += review.getScore();
                if(review.getCreatedDate().toLocalDate().equals(LocalDate.now())){
                    recentReviewCount++;
                }
            }
        }

        double[] starScores = new double[5];
        for(int i = 0; i < 5; i++){
            starScores[i] = starScoreArray[i] / (double)totalReviewCount;
        }

        return ShoppingMallReviewProductResponse.builder()
                .averageStarScore((double) starScoreSum / (double) totalReviewCount)
                .totalReviewCount(totalReviewCount)
                .recentReviewCount(recentReviewCount)
                .starScoreArray(starScores)
                .build();
    }

    public List<DetailShoppingMallReviewResponse> findShoppingMallReviewList(String productURL, String sort){
        List<Review> reviews = reviewRepository.findReviewsByProductUrlAndProjectIsNotNull(productURL);
        if (sort.equals("NEWEST")){
            reviews.sort((o1, o2) -> o2.getCreatedDate().compareTo(o1.getCreatedDate()));
        }

        if(sort.equals("BEST")){
            reviews.sort((o1, o2) ->{
                if(o1.getScore() == o2.getScore()){
                    return o2.getReactionCount() - o1.getReactionCount();
                }
                return o2.getScore() - o1.getScore();
            });
        }

        for(Review review : reviews){
            saveReviewImage(review);
        }
        return mapper.toDetailReviewResponseList(reviews);
    }

    public DetailShoppingMallReviewResponse findOneShoppingMallReview(long reviewId){
        Optional<Review> review = reviewRepository.findById(reviewId);
        if(review.isEmpty()){
            return null;
        }
        review.ifPresent(this::saveReviewImage);
        return mapper.mapReviewToDetailReviewResponse(review.get());
    }

    public void checkProductURLIsValid(String productURL) {

        if(!Pattern.matches("((https?:\\/\\/)|(\\/)|(..\\/))(\\w+:{0,1}\\w*@)?(\\S+)(:[0-9]+)?(\\/|\\/([\\w#!:.?+=&%@!\\-\\/]))?", productURL)){
            throw new ProductUrlInvalidException("유효하지 않은 URL입니다.");
        }
    }

    public void checkReviewCanEdit(User user, long reviewId){
        Optional<Review> review = reviewRepository.findById(reviewId);
        if(review.isEmpty()){
            throw new ReviewNotFoundException("입력된 리뷰아이디로 등록된 리뷰가 존재하지 않습니다");
        }
        if(!review.get().getUser().equals(user)){
            throw new CannotHandleReviewException("해당 유저의 권한으로 이 리뷰를 수정할 수 없습니다.");
        }
    }

    public DetailShoppingMallReviewResponse deleteShoppingMallReview(long reviewId){
        Optional<Review> foundReview = reviewRepository.findById(reviewId);
        if(foundReview.isPresent()){
            Review review = foundReview.get();
            review.setStatus(ReviewStatus.DELETED);
            reviewRepository.save(review);

            review.setReviewImageUuidList(new ArrayList<>());
            return mapper.mapReviewToDetailReviewResponse(review);
        }

        return null;
    }

    public DetailShoppingMallReviewResponse restoreShoppingMallReview(long reviewId){
        Optional<Review> foundReview = reviewRepository.findById(reviewId);
        if(foundReview.isPresent()){
            Review review = foundReview.get();
            review.setStatus(ReviewStatus.PENDING);
            reviewRepository.save(review);

            saveReviewImage(review);
            return mapper.mapReviewToDetailReviewResponse(review);
        }
        return null;
    }

    public DetailShoppingMallReviewResponse changeShoppingMallReview(long reviewId, ShoppingMallReviewChangeRequest changeRequest){
        Review review = reviewRepository.findById(reviewId).get();
        if(changeRequest.content() != null){
            review.setContent(changeRequest.content());
        }
        if(changeRequest.score() != null){
            review.setScore(Integer.parseInt(changeRequest.score()));
        }
        reviewRepository.save(review);

        if(changeRequest.multipartImageFiles() != null && !changeRequest.multipartImageFiles().isEmpty()){
            fileStoreService.storeFiles(changeRequest.multipartImageFiles(),review.getReviewId(),ReferenceType.REVIEW);
        }

        if(changeRequest.deleteFileList() != null && !changeRequest.deleteFileList().isEmpty()){
            fileStoreService.checkDeleteFile(changeRequest.deleteFileList());
        }

        saveReviewImage(review);
        return mapper.mapReviewToDetailReviewResponse(review);
    }

    public Project findProject(String productURL){
        Optional<Product> product = productRepository.findProductByProductUrl(productURL);
        if(product.isPresent()){
            return product.get().getProject();
        }
        // 프로젝트가 없을 경우, 프로젝트가 없다는 에러 코드를 날려야 한다.
        throw new ProjectNotFoundException("입력한 URL 에 등록된 프로젝트가 존재하지 않습니다.");
    }

    private void saveReviewImage(Review review){
        review.setReviewImageUuidList(fileStoreService.bringFileNameList(ReferenceType.REVIEW, review.getReviewId()));
    }

    public DetailReactionResponse shoppingMallReviewLikeProcess(User user, Long reviewId) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(
                () -> new ReviewNotFoundException("해당 리뷰가 존재하지 않습니다."));

        Optional<Reaction> foundReaction = reactionRepository.findByReview_ReviewIdAndUser(reviewId, user);

        Reaction reaction;
        if(foundReaction.isEmpty()){
            reaction = reactionRepository.save(Reaction.builder()
                    .review(review)
                    .user(user)
                    .build());
            review.setReactionCount(review.getReactionCount() + 1);
        }else{
            reaction = foundReaction.get();
            reactionRepository.delete(reaction);
            review.setReactionCount(review.getReactionCount() - 1);
        }
        reviewRepository.save(review);
        return mapper.toDetailReactionResponse(reaction);
    }
}