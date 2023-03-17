package com.developlife.reviewtwits.service;

import com.developlife.reviewtwits.entity.Product;
import com.developlife.reviewtwits.entity.Project;
import com.developlife.reviewtwits.entity.Review;
import com.developlife.reviewtwits.entity.User;
import com.developlife.reviewtwits.exception.project.ProjectIdNotFoundException;
import com.developlife.reviewtwits.exception.review.CannotHandleReviewException;
import com.developlife.reviewtwits.exception.review.ReviewNotExistException;
import com.developlife.reviewtwits.mapper.ReviewMapper;
import com.developlife.reviewtwits.message.request.review.ShoppingMallReviewWriteRequest;
import com.developlife.reviewtwits.message.response.review.DetailReviewResponse;
import com.developlife.reviewtwits.message.response.review.ShoppingMallReviewProductResponse;
import com.developlife.reviewtwits.repository.ProductRepository;
import com.developlife.reviewtwits.repository.ReviewRepository;
import com.developlife.reviewtwits.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * @author WhalesBob
 * @since 2023-03-13
 */

@Service
@RequiredArgsConstructor
public class ShoppingMallReviewService {

    private final ReviewMapper mapper;
    private final FileStoreService fileStoreService;
    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;

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
        fileStoreService.storeFiles(writeRequest.multipartImageFiles(), review.getReviewId(),"Review");
    }

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
        for(Review review : reviews){
            starScoreArray[review.getScore()-1]++;
            starScoreSum += review.getScore();
            if(review.getCreatedDate().toLocalDate() == LocalDate.now()){
                recentReviewCount++;
            }
        }

        int totalReviewCount = reviews.size();
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

    public List<DetailReviewResponse> findShoppingMallReviewList(String productURL){
        List<Review> reviews = reviewRepository.findReviewsByProductUrl(productURL);
        for(Review review : reviews){
            saveReviewImage(review);
        }
        return mapper.toDetailReviewResponseList(reviews);
    }

    public void checkReviewCanEdit(User user, long reviewId){
        Optional<Review> review = reviewRepository.findById(reviewId);
        if(review.isEmpty()){
            throw new ReviewNotExistException("입력된 리뷰아이디로 등록된 리뷰가 존재하지 않습니다");
        }
        if(review.get().getUser() != user){
            throw new CannotHandleReviewException("해당 유저의 권한으로 이 리뷰를 수정할 수 없습니다.");
        }
    }


    public Project findProject(String productURL){
        Optional<Product> product = productRepository.findProductByProductUrl(productURL);
        if(product.isPresent()){
            return product.get().getProject();
        }
        // 프로젝트가 없을 경우, 프로젝트가 없다는 에러 코드를 날려야 한다.
        throw new ProjectIdNotFoundException("입력한 URL 에 등록된 프로젝트가 존재하지 않습니다.");
    }

    private void saveReviewImage(Review review){
        review.setReviewImageNameList(fileStoreService.bringFileNameList("Review", review.getReviewId()));
    }
}