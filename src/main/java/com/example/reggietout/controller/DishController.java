package com.example.reggietout.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggietout.dto.DishDto;
import com.example.reggietout.pojo.Category;
import com.example.reggietout.pojo.Dish;
import com.example.reggietout.service.DishFlavorService;
import com.example.reggietout.service.DishService;
import com.example.reggietout.tools.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import com.example.reggietout.service.SortService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private SortService sortService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @PostMapping
    public Result addDish(@RequestBody DishDto dishDto) {
        log.info("添加菜品");
        dishService.saveDishAndFlavor(dishDto);
        return Result.success();
    }

    @GetMapping("/page")
    public Result page(int page, int pageSize, String name) {
        Page<Dish> page1 = new Page<>(page, pageSize);
        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(name != null, Dish::getName, name);
        lambdaQueryWrapper.orderByAsc(Dish::getPrice);
        dishService.page(page1, lambdaQueryWrapper);

        Page<DishDto> dtoPage = new Page<>();
        BeanUtils.copyProperties(page1, dtoPage, "records");

        List<Dish> records = page1.getRecords();

        List<DishDto> dishDtoList = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            Long categoryId = item.getCategoryId();
            Category category = sortService.getById(categoryId);

            if (category != null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }

            dishDto.setCreateTime(LocalDateTime.now());
            dishDto.setUpdateTime(LocalDateTime.now());
            String emp = stringRedisTemplate.opsForValue().get("employee");
            Long aLong = Long.valueOf(emp);
            dishDto.setCreateUser(aLong);
            dishDto.setUpdateUser(aLong);

            return dishDto;
        }).collect(Collectors.toList());

        dtoPage.setRecords(dishDtoList);

        return Result.success(dtoPage);
    }

    @GetMapping("/{id}")
    public Result updateDish(@PathVariable Long id){
        DishDto dishDto = dishService.selectDish(id);
        return  Result.success(dishDto);

    }

    @PutMapping
    public Result updateDish2(@RequestBody DishDto dishDto){
        log.info("修改菜品");
        dishService.updateDish(dishDto);
        return Result.success();
    }

    @DeleteMapping
    public Result deleteDish(Long ids){
        log.info("删除菜品 {}",ids);
        dishService.popDish(ids);
        return Result.success();
    }

    @GetMapping("/list")
    public Result setMealGetDish(Dish dish){
        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
        List<Dish> dishList = dishService.list(lambdaQueryWrapper);
        return Result.success(dishList);
    }

//    @DeleteMapping
//    public Result pops(@RequestParam Long[] ids){
//        log.info("ids :{}",ids.toString());
//        for (Long id : ids) {
//            dishService.removeById(id);
//            dishFlavorService.removeById(id);
//        }
//        return Result.success();
//    }
}
