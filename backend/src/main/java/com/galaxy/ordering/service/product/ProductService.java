package com.galaxy.ordering.service.product;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.galaxy.ordering.common.BusinessException;
import com.galaxy.ordering.entity.Category;
import com.galaxy.ordering.entity.Product;
import com.galaxy.ordering.mapper.CategoryMapper;
import com.galaxy.ordering.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService extends ServiceImpl<ProductMapper, Product> {

    private final CategoryMapper categoryMapper;

    public List<Product> listByMerchant(Long merchantId) {
        return this.list(new LambdaQueryWrapper<Product>()
                .eq(Product::getMerchantId, merchantId)
                .eq(Product::getStatus, "ON_SHELF"));
    }

    public List<Product> listByMerchantAndCategory(Long merchantId, Long categoryId) {
        return this.list(new LambdaQueryWrapper<Product>()
                .eq(Product::getMerchantId, merchantId)
                .eq(Product::getCategoryId, categoryId)
                .eq(Product::getStatus, "ON_SHELF"));
    }

    public List<Category> categoriesByMerchant(Long merchantId) {
        return categoryMapper.selectList(new LambdaQueryWrapper<Category>()
                .eq(Category::getMerchantId, merchantId)
                .orderByAsc(Category::getSort));
    }

    public Product create(Product product) {
        if (product.getPrice() == null || product.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("商品价格必须大于0");
        }
        this.save(product);
        return product;
    }

    public Product update(Long id, Product product) {
        Product existing = this.getById(id);
        if (existing == null) {
            throw new BusinessException("商品不存在");
        }
        product.setId(id);
        this.updateById(product);
        return product;
    }

    public void delete(Long id) {
        Product p = this.getById(id);
        if (p == null) {
            throw new BusinessException("商品不存在");
        }
        p.setStatus("OFF_SHELF");
        this.updateById(p);
    }
}
