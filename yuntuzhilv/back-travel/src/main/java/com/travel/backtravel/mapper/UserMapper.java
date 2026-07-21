package com.travel.backtravel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.travel.backtravel.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
