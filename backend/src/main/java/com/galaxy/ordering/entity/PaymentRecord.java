package com.galaxy.ordering.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.galaxy.ordering.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("payment_record")
public class PaymentRecord extends BaseEntity {
    private Long orderId;
    private String orderNo;
    private BigDecimal amount;
    private String payChannel;  // mock, alipay, wechat
    private String status;      // PENDING, SUCCESS, FAILED
    private LocalDateTime payTime;
}
