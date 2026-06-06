package com.galaxy.ordering.controller;

import com.galaxy.ordering.common.Result;
import com.galaxy.ordering.entity.Category;
import com.galaxy.ordering.entity.Product;
import com.galaxy.ordering.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/merchants/{merchantId}")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/products")
    public Result<List<Product>> listByMerchant(
            @PathVariable Long merchantId,
            @RequestParam(required = false) Long categoryId) {
        if (categoryId != null) {
            return Result.ok(productService.listByMerchantAndCategory(merchantId, categoryId));
        }
        return Result.ok(productService.listByMerchant(merchantId));
    }

    @GetMapping("/categories")
    public Result<List<Category>> categories(@PathVariable Long merchantId) {
        return Result.ok(productService.categoriesByMerchant(merchantId));
    }
}
