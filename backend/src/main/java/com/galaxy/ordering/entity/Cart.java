package com.galaxy.ordering.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.galaxy.ordering.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("cart")
public class Cart extends BaseEntity {
    private Long userId;
    private Long merchantId;
    private Long productId;
    private Integer quantity;
}
