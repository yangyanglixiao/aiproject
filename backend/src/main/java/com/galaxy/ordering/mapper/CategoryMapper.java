package com.galaxy.ordering.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.galaxy.ordering.entity.Category;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}
