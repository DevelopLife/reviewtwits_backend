package com.developlife.reviewtwits.service;

import com.developlife.reviewtwits.controller.UserController;
import com.developlife.reviewtwits.entity.Product;
import com.developlife.reviewtwits.entity.Project;
import com.developlife.reviewtwits.entity.Review;
import com.developlife.reviewtwits.entity.User;
import com.developlife.reviewtwits.exception.project.ProjectIdNotFoundException;
import com.developlife.reviewtwits.message.request.review.ShoppingMallReviewWriteRequest;
import com.developlife.reviewtwits.repository.ProductRepository;
import com.developlife.reviewtwits.repository.ReviewRepository;
import com.developlife.reviewtwits.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;

/**
 * @author WhalesBob
 * @since 2023-03-13
 */

@Service
@RequiredArgsConstructor
public class ShoppingMallReviewService {

    private final UserService userService;
    private final FileStoreService fileStoreService;
    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;

    public void saveShoppingMallReview(ShoppingMallReviewWriteRequest writeRequest) throws IOException {

        User user = userService.getUser(UserController.getTokenOwner());
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



    public Project findProject(String productURL){
        Optional<Product> product = productRepository.findProductByProductUrl(productURL);
        if(product.isPresent()){
            return product.get().getProject();
        }
        // 프로젝트가 없을 경우, 프로젝트가 없다는 에러 코드를 날려야 한다.
        throw new ProjectIdNotFoundException("입력한 URL 에 등록된 프로젝트가 존재하지 않습니다.");
    }

}