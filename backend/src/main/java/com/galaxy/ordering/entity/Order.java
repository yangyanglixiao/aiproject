package com.galaxy.ordering.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.galaxy.ordering.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("order")
public class Order extends BaseEntity {
    private String orderNo;
    private Long userId;
    private BigDecimal totalAmount;
    private String status;       // PENDING, PAID, PREPARING, DELIVERING, COMPLETED, CANCELLED
    private String payStatus;    // UNPAID, PAID
    private String payMethod;
    private LocalDateTime payTime;
}
