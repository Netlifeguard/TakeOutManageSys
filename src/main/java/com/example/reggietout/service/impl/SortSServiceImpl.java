package com.example.reggietout.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reggietout.mapper.SortMapper;
import com.example.reggietout.pojo.Category;
import com.example.reggietout.service.SortService;
import org.springframework.stereotype.Service;

@Service
public class SortSServiceImpl extends ServiceImpl<SortMapper, Category> implements SortService {
}
