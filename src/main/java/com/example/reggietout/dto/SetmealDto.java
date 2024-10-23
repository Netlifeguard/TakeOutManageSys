package com.example.reggietout.dto;


import com.example.reggietout.pojo.Setmeal;
import com.example.reggietout.pojo.SetmealDish;
import lombok.Data;

import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
