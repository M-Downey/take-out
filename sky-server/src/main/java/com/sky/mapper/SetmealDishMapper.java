package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SetmealDishMapper {
    /**
     * 根据菜品 id 查出对应的套餐 id
     * @param dishIds
     * @return
     */
    // select setmeal_id from setmeal_dish where dish_id in (1, 2, 3, 4)
    List<Long> getSetmealIdsByDishIds(List<Long> dishIds);

    /**
     * 批量新增套餐-菜品
     * @param setmealDishList
     */
    void insertBatch(List<SetmealDish> setmealDishList);

    /**
     * 根据套餐id删除套餐-菜品数据
     * @param setmealId
     */
    @Delete("delete from setmeal_dish where setmeal_id = #{setmealId}")
    void deleteBySetmealId(Long setmealId);
}
