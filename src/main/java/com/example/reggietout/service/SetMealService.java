package com.example.reggietout.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.reggietout.dto.SetmealDto;
import com.example.reggietout.pojo.Setmeal;

public interface SetMealService extends IService<Setmeal> {
    public void add(SetmealDto setmealDto);
    public void pop(Long ids);
}
