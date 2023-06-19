package com.developlife.reviewtwits.controller;

import com.developlife.reviewtwits.message.request.product.ProductRegisterRequest;
import com.developlife.reviewtwits.message.response.product.ProductRegisterResponse;
import com.developlife.reviewtwits.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author WhalesBob
 * @since 2023-05-02
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    @PostMapping("/register/{projectName}")
    public ProductRegisterResponse registerProduct(@PathVariable String projectName,
                                                   @Valid @RequestBody ProductRegisterRequest request){
        return productService.registerProductOnProject(projectName, request);
    }
}