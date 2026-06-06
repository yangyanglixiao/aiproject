package com.galaxy.ordering.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.galaxy.ordering.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
