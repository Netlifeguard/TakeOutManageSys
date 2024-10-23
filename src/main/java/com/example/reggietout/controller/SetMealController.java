package com.example.reggietout.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggietout.dto.SetmealDto;
import com.example.reggietout.pojo.Category;
import com.example.reggietout.pojo.Setmeal;
import com.example.reggietout.service.SetMealDishService;
import com.example.reggietout.service.SetMealService;
import com.example.reggietout.service.SortService;
import com.example.reggietout.tools.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetMealController {
    @Autowired
    private SetMealService setMealService;
    @Autowired
    private SetMealDishService setMealDishService;
    @Autowired
    private SortService sortService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @GetMapping("/page")
    public Result page(int page,int pageSize,String name){
        log.info("套餐信息");
        Page<Setmeal> page1 = new Page<>(page, pageSize);
        //page1中的信息即setmeal不能完全满足页面上要展示的信息，即无法显示分类名，需要扩展一下
        //所需要的分类名在setmealdto中，需要构造一个dto
        Page<SetmealDto> dtoPage = new Page<>();
        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(name!=null,Setmeal::getName,name);
        lambdaQueryWrapper.orderByAsc(Setmeal::getPrice);
        setMealService.page(page1);
        //对象拷贝，处理
        BeanUtils.copyProperties(page1,dtoPage,"records");//忽略records属性，它包含各项数据，类型不一样
        //单独对records进行处理,思路：重新构造一个完整的dto
        List<Setmeal> page1Records = page1.getRecords();
        List<SetmealDto> setmealDtoList = page1Records.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);
            Long categoryId = item.getCategoryId();
            Category category = sortService.getById(categoryId);

            if (category!=null){
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }

            setmealDto.setCreateTime(LocalDateTime.now());
            setmealDto.setUpdateTime(LocalDateTime.now());
            String emp = stringRedisTemplate.opsForValue().get("employee");
            Long aLong = Long.valueOf(emp);
            setmealDto.setCreateUser(aLong);
            setmealDto.setUpdateUser(aLong);

            return setmealDto;
        }).collect(Collectors.toList());

        dtoPage.setRecords(setmealDtoList);

        return Result.success(dtoPage);
    }

    @PostMapping
    public Result addSetMeal(@RequestBody SetmealDto setmealDto){
         setMealService.add(setmealDto);
         return Result.success();
    }

    @DeleteMapping
    public Result pop(Long ids){
         setMealService.pop(ids);
         return Result.success();
    }



}
