package com.galaxy.ordering.service.merchant;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.galaxy.ordering.common.BusinessException;
import com.galaxy.ordering.entity.Merchant;
import com.galaxy.ordering.mapper.MerchantMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MerchantService extends ServiceImpl<MerchantMapper, Merchant> {

    public Page<Merchant> list(int page, int size, String keyword) {
        Page<Merchant> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Merchant> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Merchant::getName, keyword);
        }
        wrapper.eq(Merchant::getStatus, "APPROVED")
               .orderByDesc(Merchant::getCreatedTime);
        return this.page(pageParam, wrapper);
    }

    public Merchant getById(Long id) {
        Merchant merchant = this.getByIdSimple(id);
        if (merchant == null || !"APPROVED".equals(merchant.getStatus())) {
            throw new BusinessException("商家不存在或未通过审核");
        }
        return merchant;
    }

    public Merchant getByIdSimple(Long id) {
        return this.getById(id);
    }

    public List<Merchant> listAllApproved() {
        return this.list(new LambdaQueryWrapper<Merchant>()
                .eq(Merchant::getStatus, "APPROVED")
                .orderByDesc(Merchant::getCreatedTime));
    }

    public Merchant create(Merchant merchant) {
        merchant.setStatus("REVIEWING");
        this.save(merchant);
        return merchant;
    }

    public Merchant audit(Long id, String status) {
        if (!"APPROVED".equals(status) && !"REJECTED".equals(status)) {
            throw new BusinessException("审核状态只能是 APPROVED 或 REJECTED");
        }
        Merchant merchant = this.getById(id);
        if (merchant == null) {
            throw new BusinessException("商家不存在");
        }
        merchant.setStatus(status);
        this.updateById(merchant);
        return merchant;
    }
}
