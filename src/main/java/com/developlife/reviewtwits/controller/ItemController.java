package com.developlife.reviewtwits.controller;

import com.developlife.reviewtwits.service.itemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * @author ghdic
 * @since 2023/03/22
 */
@Slf4j
@RestController
@RequestMapping("/items")
public class ItemController {
    private itemService itemService;

    public ItemController(itemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping(value = "/register-crawling")
    public void registerCrawling(@RequestParam String productName) throws IOException {
        itemService.relateProductsCrawling(productName);
    }

    @GetMapping(value = "/search", produces = "application/json; charset=utf8")
    public String search(@RequestParam String productName) {
        return itemService.search(productName);
    }
}
