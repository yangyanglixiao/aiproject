package com.galaxy.ordering.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.galaxy.ordering.entity.Product;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {
}
