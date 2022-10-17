package com.reggie.dto;

import com.reggie.entity.SetMeal;
import com.reggie.entity.SetMealDish;
import lombok.Data;

import java.util.List;

/**
 * @author m0v1
 * @date 2022年10月16日 11:02
 */
@Data
public class SetMealDto extends SetMeal {

    private List<SetMealDish> setmealDishes;// 为了与前端请求参数一致,将setMealDishes命名为setmealDishes

    private String categoryName;
}

