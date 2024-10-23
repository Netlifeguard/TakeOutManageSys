package com.example.reggietout.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.reggietout.dto.DishDto;
import com.example.reggietout.pojo.Dish;

public interface DishService extends IService<Dish> {
    //需要同时操作两张表，扩展mp
    public void saveDishAndFlavor(DishDto dishDto);
    public DishDto selectDish(Long id);
    public void updateDish(DishDto dishDto);
    public void popDish(Long ids);
    public void popByGroup();

}
