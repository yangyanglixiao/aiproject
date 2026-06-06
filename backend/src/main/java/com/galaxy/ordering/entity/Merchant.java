package com.galaxy.ordering.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.galaxy.ordering.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("merchant")
public class Merchant extends BaseEntity {
    private String name;
    private String address;
    private String phone;
    private String logo;
    private String description;
    private String status;  // REVIEWING, APPROVED, REJECTED
}
