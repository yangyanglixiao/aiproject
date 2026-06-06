package com.galaxy.ordering.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.galaxy.ordering.common.Result;
import com.galaxy.ordering.entity.Merchant;
import com.galaxy.ordering.service.merchant.MerchantService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/merchants")
@RequiredArgsConstructor
public class AdminMerchantController {

    private final MerchantService merchantService;

    @GetMapping
    public Result<Page<Merchant>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        Page<Merchant> pageParam = new Page<>(page, size);
        return Result.ok(merchantService.page(pageParam, new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Merchant>()
                .orderByDesc(Merchant::getCreateTime)));
    }

    @PutMapping("/{id}/audit")
    public Result<Merchant> audit(@PathVariable Long id, @RequestBody AuditRequest request) {
        return Result.ok(merchantService.audit(id, request.getStatus()));
    }

    public static class AuditRequest {
        private String status;
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
}
