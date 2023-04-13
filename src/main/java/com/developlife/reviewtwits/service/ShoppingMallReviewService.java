package com.developlife.reviewtwits.service;

import com.developlife.reviewtwits.entity.Product;
import com.developlife.reviewtwits.entity.Project;
import com.developlife.reviewtwits.entity.Review;
import com.developlife.reviewtwits.entity.User;
import com.developlife.reviewtwits.exception.project.ProjectIdNotFoundException;
import com.developlife.reviewtwits.exception.review.CannotHandleReviewException;
import com.developlife.reviewtwits.exception.review.ReviewNotFoundException;
import com.developlife.reviewtwits.mapper.ReviewMapper;
import com.developlife.reviewtwits.message.request.review.ShoppingMallReviewChangeRequest;
import com.developlife.reviewtwits.message.request.review.ShoppingMallReviewWriteRequest;
import com.developlife.reviewtwits.message.response.review.DetailShoppingMallReviewResponse;
import com.developlife.reviewtwits.message.response.review.ShoppingMallReviewProductResponse;
import com.developlife.reviewtwits.repository.ProductRepository;
import com.developlife.reviewtwits.repository.ReviewRepository;
import com.developlife.reviewtwits.type.FileReferenceType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.BindException;
import java.time.LocalDate;
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

    @Transactional
    public void saveShoppingMallReview(ShoppingMallReviewWriteRequest writeRequest, User user) throws IOException {

        Project project = findProject(writeRequest.productURL());

        Review review = Review.builder()
                .user(user)
                .project(project)
                .content(writeRequest.content())
                .productUrl(writeRequest.productURL())
                .score(Integer.parseInt(writeRequest.score()))
                .build();

        reviewRepository.save(review);

        log.info("writeRequest.multipartImageFiles() = {}",writeRequest.multipartImageFiles());
        log.info("writeRequest.multipartImageFiles() null 여부 = {}", writeRequest.multipartImageFiles() == null);
        if(writeRequest.multipartImageFiles() != null) {
            fileStoreService.storeFiles(writeRequest.multipartImageFiles(), review.getReviewId(), FileReferenceType.REVIEW);
        }
    }

    @Transactional(readOnly = true)
    public ShoppingMallReviewProductResponse findShoppingMallReviewTotalInfo(String productURL){
        if(!productRepository.existsProductByProductUrl(productURL)){
            return null;
        }

        List<Review> reviews = reviewRepository.findReviewsByProductUrl(productURL);

        if(reviews.isEmpty()){
            return ShoppingMallReviewProductResponse.builder().totalReviewCount(0).build();
        }

        int starScoreSum = 0;
        int[] starScoreArray = new int[5];
        int recentReviewCount = 0;
        int totalReviewCount = 0;
        for(Review review : reviews){
            if(review.isExist()){
                totalReviewCount++;
                starScoreArray[review.getScore()-1]++;
                starScoreSum += review.getScore();
                if(review.getCreatedDate().toLocalDate() == LocalDate.now()){
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

    @Transactional(readOnly = true)
    public List<DetailShoppingMallReviewResponse> findShoppingMallReviewList(String productURL){
        List<Review> reviews = reviewRepository.findReviewsByProductUrl(productURL);
        for(Review review : reviews){
            saveReviewImage(review);
        }
        return mapper.toDetailReviewResponseList(reviews);
    }

    @Transactional(readOnly = true)
    public DetailShoppingMallReviewResponse findOneShoppingMallReview(long reviewId){
        Optional<Review> review = reviewRepository.findById(reviewId);
        if(review.isEmpty()){
            return null;
        }
        review.ifPresent(this::saveReviewImage);
        return mapper.mapReviewToDetailReviewResponse(review.get());
    }

    public void checkProductURLIsValid(String productURL) throws BindException {
        if(!Pattern.matches("^(https?://)[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+/[a-zA-Z0-9-_/.?=]*",productURL)){
            throw new BindException("유효한 productURL 이 아닙니다.");
        }
    }

    @Transactional(readOnly = true)
    public void checkReviewCanEdit(User user, long reviewId){
        Optional<Review> review = reviewRepository.findById(reviewId);
        if(review.isEmpty()){
            throw new ReviewNotFoundException("입력된 리뷰아이디로 등록된 리뷰가 존재하지 않습니다");
        }
        if(!review.get().getUser().equals(user)){
            throw new CannotHandleReviewException("해당 유저의 권한으로 이 리뷰를 수정할 수 없습니다.");
        }
    }

    @Transactional
    public void deleteShoppingMallReview(long reviewId){
        Optional<Review> foundReview = reviewRepository.findById(reviewId);
        if(foundReview.isPresent()){
            Review review = foundReview.get();
            review.setExist(false);
            reviewRepository.save(review);
        }
    }

    @Transactional
    public void restoreShoppingMallReview(long reviewId){
        Optional<Review> foundReview = reviewRepository.findById(reviewId);
        if(foundReview.isPresent()){
            Review review = foundReview.get();
            review.setExist(true);
        }
    }

    @Transactional
    public void changeShoppingMallReview(long reviewId, ShoppingMallReviewChangeRequest changeRequest) throws IOException {
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

    @Transactional(readOnly = true)
    public Project findProject(String productURL){
        Optional<Product> product = productRepository.findProductByProductUrl(productURL);
        if(product.isPresent()){
            return product.get().getProject();
        }
        // 프로젝트가 없을 경우, 프로젝트가 없다는 에러 코드를 날려야 한다.
        throw new ProjectIdNotFoundException("입력한 URL 에 등록된 프로젝트가 존재하지 않습니다.");
    }

    private void saveReviewImage(Review review){
        review.setReviewImageNameList(fileStoreService.bringFileNameList(FileReferenceType.REVIEW, review.getReviewId()));
    }
}