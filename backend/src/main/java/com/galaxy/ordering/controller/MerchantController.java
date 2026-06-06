package com.galaxy.ordering.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.galaxy.ordering.common.Result;
import com.galaxy.ordering.entity.Merchant;
import com.galaxy.ordering.service.merchant.MerchantService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/merchants")
@RequiredArgsConstructor
public class MerchantController {

    private final MerchantService merchantService;

    @GetMapping
    public Result<Page<Merchant>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        return Result.ok(merchantService.list(page, size, keyword));
    }

    @GetMapping("/{id}")
    public Result<Merchant> detail(@PathVariable Long id) {
        return Result.ok(merchantService.getById(id));
    }

    @PostMapping
    public Result<Merchant> create(@RequestBody Merchant merchant) {
        return Result.ok(merchantService.create(merchant));
    }
}
