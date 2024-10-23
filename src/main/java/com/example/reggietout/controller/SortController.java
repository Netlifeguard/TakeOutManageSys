package com.example.reggietout.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggietout.pojo.Category;
import com.example.reggietout.service.SortService;
import com.example.reggietout.tools.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/category")
public class SortController {
    @Autowired
    private SortService sortService;
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @PostMapping
    public Result addDishBrand(@RequestBody Category category){
        log.info("添加新菜品");
        category.setCreateTime(LocalDateTime.now());
        category.setUpdateTime(LocalDateTime.now());
        String emp = stringRedisTemplate.opsForValue().get("employee");
        Long aLong = Long.valueOf(emp);
        category.setCreateUser(aLong);
        category.setUpdateUser(aLong);

        sortService.save(category);
        return Result.success();
    }

    @GetMapping("/page")
    public Result pageQuery(int page,int pageSize){
        Page<Category> page1 = new Page<>(page,pageSize);
        LambdaQueryWrapper<Category> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.orderByAsc(Category::getSort);
        sortService.page(page1,lambdaQueryWrapper);
        return Result.success(page1);

    }

    @DeleteMapping
    public Result deleteDish(String ids){
        log.info("删除分类 {}",ids);
        sortService.removeById(ids);
        return Result.success();
    }

    @PutMapping
    public Result udpateSort(@RequestBody Category category){
        log.info("修改");
        category.setCreateTime(LocalDateTime.now());
        category.setUpdateTime(LocalDateTime.now());
        String emp = stringRedisTemplate.opsForValue().get("employee");
        Long aLong = Long.valueOf(emp);
        category.setCreateUser(aLong);
        category.setUpdateUser(aLong);
        sortService.updateById(category);
        return Result.success();
    }

    @GetMapping("/list")
    public Result addDish(int type){
        LambdaQueryWrapper<Category> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Category::getType,type);

        List<Category> list = sortService.list(lambdaQueryWrapper);

        return Result.success(list);

    }

}
