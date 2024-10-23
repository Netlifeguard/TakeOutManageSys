package com.example.reggietout.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.reggietout.pojo.Category;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SortMapper extends BaseMapper<Category> {
}
