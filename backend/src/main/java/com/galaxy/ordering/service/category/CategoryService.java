package com.galaxy.ordering.service.category;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.galaxy.ordering.entity.Category;
import com.galaxy.ordering.mapper.CategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService extends ServiceImpl<CategoryMapper, Category> {

    public List<Category> listByMerchant(Long merchantId) {
        return this.list(new LambdaQueryWrapper<Category>()
                .eq(Category::getMerchantId, merchantId)
                .orderByAsc(Category::getSort));
    }

    public void delete(Long id) {
        this.removeById(id);
    }
}
