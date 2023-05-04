package com.developlife.reviewtwits.service;

import com.developlife.reviewtwits.entity.Product;
import com.developlife.reviewtwits.entity.StatInfo;
import com.developlife.reviewtwits.entity.User;
import com.developlife.reviewtwits.exception.product.ProductNotRegisteredException;
import com.developlife.reviewtwits.mapper.StatMapper;
import com.developlife.reviewtwits.message.request.StatMessageRequest;
import com.developlife.reviewtwits.message.response.statistics.SaveStatResponse;
import com.developlife.reviewtwits.repository.ProductRepository;
import com.developlife.reviewtwits.repository.project.StatInfoRepository;
import com.developlife.reviewtwits.type.project.Device;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author WhalesBob
 * @since 2023-04-23
 */
@Service
@RequiredArgsConstructor
public class StatService {

    private final StatInfoRepository statInfoRepository;
    private final ProductRepository productRepository;
    private final StatMapper statMapper;

    public SaveStatResponse saveStatInfo(User user, StatMessageRequest statMessageRequest) {

        Device device = Device.valueOf(statMessageRequest.device());

        Product foundProduct = productRepository.findProductByProductUrl(statMessageRequest.productUrl())
                .orElseThrow(() -> new ProductNotRegisteredException("해당 상품이 존재하지 않습니다."));

        StatInfo statInfo = StatInfo.builder()
                .user(user)
                .device(device)
                .inflowUrl(statMessageRequest.inflowUrl())
                .productUrl(statMessageRequest.productUrl())
                .product(foundProduct)
                .project(foundProduct.getProject())
                .build();

        StatInfo savedInfo = statInfoRepository.save(statInfo);
        return statMapper.mapStatInfoToSaveStatResponse(savedInfo);
    }
}