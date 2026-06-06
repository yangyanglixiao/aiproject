package com.galaxy.ordering.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.galaxy.ordering.common.Result;
import com.galaxy.ordering.entity.Product;
import com.galaxy.ordering.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
public class AdminProductController {

    private final ProductService productService;

    @GetMapping
    public Result<List<Product>> list(@RequestParam(required = false) Long merchantId) {
        if (merchantId != null) {
            return Result.ok(productService.list(new LambdaQueryWrapper<Product>()
                    .eq(Product::getMerchantId, merchantId)));
        }
        return Result.ok(productService.list(new LambdaQueryWrapper<>()));
    }

    @PostMapping
    public Result<Product> create(@RequestBody Product product) {
        return Result.ok(productService.create(product));
    }

    @PutMapping("/{id}")
    public Result<Product> update(@PathVariable Long id, @RequestBody Product product) {
        return Result.ok(productService.update(id, product));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return Result.ok();
    }
}
