package com.galaxy.ordering.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.galaxy.ordering.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderItemMapper extends BaseMapper<OrderItem> {
}
