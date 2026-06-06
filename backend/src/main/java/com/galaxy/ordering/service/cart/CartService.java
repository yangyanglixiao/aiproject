package com.galaxy.ordering.service.cart;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.galaxy.ordering.common.BusinessException;
import com.galaxy.ordering.dto.CartAddRequest;
import com.galaxy.ordering.entity.Cart;
import com.galaxy.ordering.entity.Product;
import com.galaxy.ordering.mapper.CartMapper;
import com.galaxy.ordering.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService extends ServiceImpl<CartMapper, Cart> {

    private final ProductMapper productMapper;

    public List<Cart> listByUser(Long userId) {
        return this.list(new LambdaQueryWrapper<Cart>().eq(Cart::getUserId, userId));
    }

    public Cart addItem(Long userId, CartAddRequest request) {
        Product product = productMapper.selectById(request.getProductId());
        if (product == null || !"ON_SHELF".equals(product.getStatus())) {
            throw new BusinessException("商品不存在或未上架");
        }
        if (!product.getMerchantId().equals(request.getMerchantId())) {
            throw new BusinessException("商品与商家不匹配");
        }

        Cart existing = this.getOne(new LambdaQueryWrapper<Cart>()
                .eq(Cart::getUserId, userId)
                .eq(Cart::getMerchantId, request.getMerchantId())
                .eq(Cart::getProductId, request.getProductId()));

        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + request.getQuantity());
            this.updateById(existing);
            return existing;
        }

        Cart cart = new Cart();
        cart.setUserId(userId);
        cart.setMerchantId(request.getMerchantId());
        cart.setProductId(request.getProductId());
        cart.setQuantity(request.getQuantity());
        this.save(cart);
        return cart;
    }

    public Cart update(Long id, Integer quantity) {
        Cart cart = this.getById(id);
        if (cart == null) {
            throw new BusinessException("购物车商品不存在");
        }
        if (quantity <= 0) {
            this.removeById(id);
            return null;
        }
        cart.setQuantity(quantity);
        this.updateById(cart);
        return cart;
    }

    public void remove(Long id) {
        this.removeById(id);
    }

    public void clearByMerchant(Long userId, Long merchantId) {
        this.remove(new LambdaQueryWrapper<Cart>()
                .eq(Cart::getUserId, userId)
                .eq(Cart::getMerchantId, merchantId));
    }
}
