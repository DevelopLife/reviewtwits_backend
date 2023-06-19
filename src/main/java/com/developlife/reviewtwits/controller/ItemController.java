package com.developlife.reviewtwits.controller;

import com.developlife.reviewtwits.entity.RelatedProduct;
import com.developlife.reviewtwits.service.ItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;

/**
 * @author ghdic
 * @since 2023/03/22
 */
@Slf4j
@RestController
@RequestMapping("/items")
public class ItemController {
    private ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping(value = "/request-crawling")
    public RelatedProduct registerCrawling(@RequestParam String productName) {
        return itemService.requestCrawlingProductInfo(productName);
    }

    @GetMapping(value = "/search", produces = "application/json; charset=utf8")
    public String search(@RequestParam
                         @NotBlank(message = "검색할 상품이름을 입력해주세요")
                         String productName) {
        return itemService.search(productName);
    }
}
