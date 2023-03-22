package com.developlife.reviewtwits.controller;

import com.developlife.reviewtwits.service.ProductService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author ghdic
 * @since 2023/03/22
 */
@RestController
@RequestMapping("/products")
public class ProductController {
    private ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/register-crawling")
    public void registerCrawling(@RequestParam String productName) {
        productService.relateProductsCrawling(productName);
    }

    @GetMapping(value = "/search", produces = "application/json; charset=utf8")
    public String search(@RequestParam String productName) {
        return productService.search(productName);
    }
}
