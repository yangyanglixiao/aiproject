package com.galaxy.ordering.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.galaxy.ordering.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("product")
public class Product extends BaseEntity {
    private Long merchantId;
    private Long categoryId;
    private String name;
    private String description;
    private BigDecimal price;
    private String image;
    private String status;  // ON_SHELF, OFF_SHELF
}
