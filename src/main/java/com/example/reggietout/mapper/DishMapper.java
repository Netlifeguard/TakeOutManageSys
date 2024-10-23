package com.example.reggietout.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.reggietout.pojo.Dish;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DishMapper extends BaseMapper<Dish> {
}
