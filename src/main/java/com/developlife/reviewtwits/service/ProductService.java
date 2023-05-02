package com.developlife.reviewtwits.service;

import com.developlife.reviewtwits.entity.Product;
import com.developlife.reviewtwits.entity.Project;
import com.developlife.reviewtwits.entity.User;
import com.developlife.reviewtwits.exception.project.ProjectIdNotFoundException;
import com.developlife.reviewtwits.exception.user.AccessDeniedException;
import com.developlife.reviewtwits.message.request.product.ProductRegisterRequest;
import com.developlife.reviewtwits.message.response.product.ProductRegisterResponse;
import com.developlife.reviewtwits.repository.ProductRepository;
import com.developlife.reviewtwits.repository.project.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author WhalesBob
 * @since 2023-05-02
 */
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProjectRepository projectRepository;

    public ProductRegisterResponse registerProductOnProject(User user, ProductRegisterRequest request) {

        Project project = projectRepository.findByProjectId(request.projectId())
                .orElseThrow(() -> new ProjectIdNotFoundException("존재하지 않는 프로젝트입니다."));

        if(!project.getUser().equals(user)){
            throw new AccessDeniedException("프로젝트에 대한 권한이 없습니다.");
        }

        Product savedProduct = productRepository.save(Product.builder()
                .project(project)
                .productUrl(request.productUrl())
                .build());

        return ProductRegisterResponse.builder()
                .productId(savedProduct.getProductId())
                .projectId(savedProduct.getProject().getProjectId())
                .productUrl(savedProduct.getProductUrl())
                .build();
    }
}