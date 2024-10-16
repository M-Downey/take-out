package com.sky.controller.admin;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;


@RestController("adminShopController")
@Slf4j
@Api(tags = "店铺相关接口")
@RequestMapping("/admin/shop")
public class ShopController {

    public static final String KEY = "SHOP_STATUS";

    @Autowired
    RedisTemplate redisTemplate;

    /**
     * 设置店铺营业状态
     * @param status
     * @return
     */
    @PutMapping("/{status}")
    @ApiOperation("设置店铺营业状态")
    public Result setStatus(@PathVariable Integer status){
        log.info("设置店铺营业状态为：{}", status == 1 ? "营业中": "打烊中");
        redisTemplate.opsForValue().set(KEY, status);
        return Result.success();
    }

    /**
     * 获取店铺营业状态
     * @return
     */
    @GetMapping("/status")
    @ApiOperation("获取店铺营业状态")
    public Result<Integer> getStatus(){
        // 使用互斥锁避免缓存击穿
        // 互斥锁就是加一个会失效的键值对，添加成功就上锁成功
        String lockKey = "lockKey";
        String lockValue = "lockValue";
        try {
            // 尝试加锁
            Boolean acquired = redisTemplate.opsForValue().setIfAbsent(lockKey, lockValue, 30, TimeUnit.SECONDS);
            if (acquired != null && acquired) {
                Integer status = (Integer) redisTemplate.opsForValue().get(KEY);
                log.info("管理端获取店铺的营业状态：{}", status == 1 ? "营业中": "打烊中");
                return Result.success(status);
            } else {
                log.info("获取redis互斥锁失败");
                return Result.error("获得redis互斥锁失败");
            }
        } finally {
            // 释放锁
            if (redisTemplate.hasKey(lockKey)) {
                redisTemplate.delete(lockKey);
            }
            log.info("释放redis互斥锁");
        }
//        Integer status = (Integer) redisTemplate.opsForValue().get(KEY);
//        log.info("管理端获取店铺的营业状态：{}", status == 1 ? "营业中": "打烊中");
//        return Result.success(status);
    }
}
