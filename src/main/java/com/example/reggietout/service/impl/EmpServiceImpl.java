package com.example.reggietout.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reggietout.mapper.EmpMapper;
import com.example.reggietout.pojo.Employee;
import com.example.reggietout.service.EmpService;
import org.springframework.stereotype.Service;

@Service
public class EmpServiceImpl extends ServiceImpl<EmpMapper, Employee> implements EmpService {
}
