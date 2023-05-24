package com.developlife.reviewtwits.service;

import com.developlife.reviewtwits.entity.FileInfo;
import com.developlife.reviewtwits.entity.Product;
import com.developlife.reviewtwits.entity.Project;
import com.developlife.reviewtwits.entity.User;
import com.developlife.reviewtwits.exception.product.ProductAlreadyRegisteredException;
import com.developlife.reviewtwits.exception.project.ProjectNotFoundException;
import com.developlife.reviewtwits.exception.user.AccessDeniedException;
import com.developlife.reviewtwits.message.request.product.ProductRegisterRequest;
import com.developlife.reviewtwits.message.response.product.ProductRegisterResponse;
import com.developlife.reviewtwits.repository.ProductRepository;
import com.developlife.reviewtwits.repository.project.ProjectRepository;
import com.developlife.reviewtwits.type.ReferenceType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author WhalesBob
 * @since 2023-05-02
 */
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProjectRepository projectRepository;
    private final FileStoreService fileStoreService;

    private final String REQUEST_IMAGES = "/request-images/";

    public ProductRegisterResponse registerProductOnProject(String projectName, ProductRegisterRequest request) {

        Project project = projectRepository.findByProjectName(projectName)
                .orElseThrow(() -> new ProjectNotFoundException("존재하지 않는 프로젝트입니다."));

        Optional<Product> foundProduct = productRepository.findProductByProductUrl(request.productUrl());
        if(foundProduct.isPresent()){
            throw new ProductAlreadyRegisteredException("해당 url 로 이미 제품이 등록되어 있습니다.");
        }

        Product savedProduct = getSavedProduct(projectName, request, project);

        return ProductRegisterResponse.builder()
                .productId(savedProduct.getProductId())
                .projectId(savedProduct.getProject().getProjectId())
                .productUrl(savedProduct.getProductUrl())
                .productName(savedProduct.getProductName())
                .imageUrl(savedProduct.getImageUrl())
                .build();
    }

    private Product getSavedProduct(String projectName, ProductRegisterRequest request, Project project) {
        Product savedProduct = productRepository.save(Product.builder()
                .project(project)
                .productName(request.productName())
                .productUrl(request.productUrl())
                .build());

        FileInfo savedImage = fileStoreService.downloadImageFileFromUrl(
                request.imageUrl(),
                projectName,
                request.productName(),
                savedProduct.getProductId(),
                ReferenceType.PRODUCT
        );

        savedProduct.setImageUrl(REQUEST_IMAGES + savedImage.getRealFilename());
        productRepository.save(savedProduct);
        return savedProduct;
    }
}