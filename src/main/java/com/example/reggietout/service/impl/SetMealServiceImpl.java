package com.example.reggietout.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reggietout.dto.SetmealDto;
import com.example.reggietout.mapper.SetMealMapper;
import com.example.reggietout.pojo.Setmeal;
import com.example.reggietout.pojo.SetmealDish;
import com.example.reggietout.service.SetMealDishService;
import com.example.reggietout.service.SetMealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SetMealServiceImpl extends ServiceImpl<SetMealMapper, Setmeal> implements SetMealService {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private SetMealDishService setMealDishService;

    @Override
    public void pop(Long ids) {
        this.removeById(ids);
        setMealDishService.removeById(ids);
    }

    @Override
    @Transactional
    public void add(SetmealDto setmealDto) {
        setmealDto.setCreateTime(LocalDateTime.now());
        setmealDto.setUpdateTime(LocalDateTime.now());
        String emp = stringRedisTemplate.opsForValue().get("employee");
        Long aLong = Long.valueOf(emp);
        setmealDto.setCreateUser(aLong);
        setmealDto.setUpdateUser(aLong);
        this.save(setmealDto);

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.forEach((item)->{
            item.setSetmealId(setmealDto.getId());
            item.setCreateTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());
            item.setCreateUser(aLong);
            item.setUpdateUser(aLong);
        });

        setMealDishService.saveBatch(setmealDishes);
    }
}
