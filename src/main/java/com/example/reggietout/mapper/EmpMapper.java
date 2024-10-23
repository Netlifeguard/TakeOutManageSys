package com.example.reggietout.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.reggietout.pojo.Employee;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmpMapper extends BaseMapper<Employee> {
}
