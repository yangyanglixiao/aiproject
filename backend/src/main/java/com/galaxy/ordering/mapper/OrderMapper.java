package com.galaxy.ordering.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.galaxy.ordering.entity.Order;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {
}
