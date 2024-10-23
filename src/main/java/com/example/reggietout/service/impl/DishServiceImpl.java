package com.example.reggietout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reggietout.dto.DishDto;
import com.example.reggietout.mapper.DishMapper;
import com.example.reggietout.pojo.Dish;
import com.example.reggietout.pojo.DishFlavor;
import com.example.reggietout.service.DishFlavorService;
import com.example.reggietout.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional//多张表要事物管理
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void popDish(Long ids) {
        Dish dish = this.getById(ids);
        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(DishFlavor::getDishId,dish.getId());
        dishFlavorService.remove(lambdaQueryWrapper);
        this.removeById(ids);
    }

    @Override
    public void popByGroup() {

    }

    @Override
    public void updateDish(DishDto dishDto) {
        //先删除
        LambdaQueryWrapper<DishFlavor>  lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(lambdaQueryWrapper);
        this.removeById(dishDto.getId());
        //再插入
        this.saveDishAndFlavor(dishDto);
    }

    @Override
    public DishDto selectDish(Long id) {
        //分别查出来，然后对象拷贝
        Dish byId = this.getById(id);
        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(DishFlavor::getDishId,byId.getId());
        List<DishFlavor> flavors = dishFlavorService.list(lambdaQueryWrapper);

        DishDto dto = new DishDto();
        BeanUtils.copyProperties(byId,dto);
        dto.setFlavors(flavors);
        return dto;

    }

    @Override
    public void saveDishAndFlavor(DishDto dishDto) {

        String emp = stringRedisTemplate.opsForValue().get("employee");
        Long aLong = Long.valueOf(emp);
        //保持到菜品表
        dishDto.setCreateTime(LocalDateTime.now());
        dishDto.setUpdateTime(LocalDateTime.now());
        dishDto.setCreateUser(aLong);
        dishDto.setUpdateUser(aLong);
        this.save(dishDto);
        //其中的口味要保存到口味表，先注入对应的service
        //表单中只有两个值，此时其他的值也是需要赋的，额外处理
        //上面save的时候其实就有所有值了，只是flavor集合中没有
        //对集合中的元素重新加工一下
        Long dishId = dishDto.getId();
        List<DishFlavor> dishFlavors = dishDto.getFlavors();
        List<DishFlavor> dishFlavorList = dishFlavors.stream().map((item) -> {
            item.setDishId(dishId);
            item.setCreateTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());
            item.setCreateUser(aLong);
            item.setUpdateUser(aLong);
            return item;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(dishFlavorList);
    }
}
