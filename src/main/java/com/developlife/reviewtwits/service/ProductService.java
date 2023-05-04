package com.developlife.reviewtwits.service;

import com.developlife.reviewtwits.entity.FileInfo;
import com.developlife.reviewtwits.entity.Product;
import com.developlife.reviewtwits.entity.Project;
import com.developlife.reviewtwits.entity.User;
import com.developlife.reviewtwits.exception.project.ProjectNotFoundException;
import com.developlife.reviewtwits.exception.user.AccessDeniedException;
import com.developlife.reviewtwits.message.request.product.ProductRegisterRequest;
import com.developlife.reviewtwits.message.response.product.ProductRegisterResponse;
import com.developlife.reviewtwits.repository.ProductRepository;
import com.developlife.reviewtwits.repository.project.ProjectRepository;
import com.developlife.reviewtwits.type.ReferenceType;
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
    private final FileStoreService fileStoreService;

    private final String REQUEST_IMAGES = "/request-images/";

    public ProductRegisterResponse registerProductOnProject(User user, String projectName, ProductRegisterRequest request) {

        Project project = projectRepository.findByProjectName(projectName)
                .orElseThrow(() -> new ProjectNotFoundException("존재하지 않는 프로젝트입니다."));

        if(!project.getUser().equals(user)){
            throw new AccessDeniedException("프로젝트에 대한 권한이 없습니다.");
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