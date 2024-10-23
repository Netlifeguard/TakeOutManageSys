package com.example.reggietout.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggietout.config.RedisConfig;
import com.example.reggietout.pojo.Employee;
import com.example.reggietout.service.EmpService;
import com.example.reggietout.tools.Result;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmpController {
    @Autowired
    private EmpService empService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;



    @PostMapping("/login")
    public Result login(@RequestBody Employee employee){
        String password = employee.getPassword();
        password= DigestUtils.md5DigestAsHex(password.getBytes());

        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = empService.getOne(queryWrapper);

        System.out.println(emp.getUsername());
        if (emp==null)
            return Result.error("no user");
        if (emp.getStatus()!=1)
            return Result.error("user disable");
        if (!emp.getPassword().equals(password))
            return Result.error("pwd error");

        stringRedisTemplate.opsForValue().set("user",emp.getUsername());
        stringRedisTemplate.opsForValue().set("userId",String.valueOf(emp.getId()));

        String user = stringRedisTemplate.opsForValue().get("user");
        String userId = stringRedisTemplate.opsForValue().get("userId");

        if (user!= null) {
            // 如果键存在且值不为空
            System.out.println("user is: " + user);
        } else {
            // 如果键不存在或值为空
            System.out.println("user does not exist or is null.");
        }
        if (userId!= null) {
            // 如果键存在且值不为空
            System.out.println("userId is: " + userId);
        } else {
            // 如果键不存在或值为空
            System.out.println("userid does not exist or is null.");
        }

        return Result.success(emp);
    }

    @PostMapping("/logout")
    public Result logout(){
        stringRedisTemplate.delete("user");
        stringRedisTemplate.delete("userId");
        return Result.success("logouting");
    }

    @PostMapping
    public Result insertEmp(@RequestBody Employee employee){

        log.info("新增员工：{}",employee.getUsername());
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());
        String emp = stringRedisTemplate.opsForValue().get("employee");
        Long aLong = Long.valueOf(emp);
        employee.setCreateUser(aLong);
        employee.setUpdateUser(aLong);

        empService.save(employee);
        return Result.success("已保存");
    }

    @GetMapping("/page")
    public Result pageQuery(int page,int pageSize,String name){//使用mp提供的分页插件
        Page<Employee> pageInfo = new Page<>(page,pageSize);
        LambdaQueryWrapper<Employee> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(!StringUtils.isEmpty(name),Employee::getName,name);
        lambdaQueryWrapper.orderByDesc(Employee::getCreateTime);

        empService.page(pageInfo,lambdaQueryWrapper);


        return Result.success(pageInfo);
    }

    @PutMapping
    public Result empAccountManage(@RequestBody Employee employee){
        String userId = stringRedisTemplate.opsForValue().get("userId");
        employee.setUpdateTime(LocalDateTime.now());
        employee.setUpdateUser(Long.valueOf(userId));
        empService.updateById(employee);
        return Result.success("账号操作成功");
    }

    @GetMapping("/{id}")
    public Result updateEmp(@PathVariable String id){
        log.info("收到id {}",id);
        LambdaQueryWrapper<Employee> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Employee::getId,id);
        Employee employee = empService.getOne(lambdaQueryWrapper);
        return Result.success(employee);
    }
}
