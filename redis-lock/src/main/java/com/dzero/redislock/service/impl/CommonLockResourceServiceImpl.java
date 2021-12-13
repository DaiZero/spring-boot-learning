package com.dzero.redislock.service.impl;

import com.dzero.redislock.service.CommonLockResourceService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

/**
 * CommonLockResourceServiceImpl
 *
 * @author DaiZedong
 */
@Service
@Transactional
public class CommonLockResourceServiceImpl implements CommonLockResourceService {
    private final StringRedisTemplate stringRedisTemplate;

    public CommonLockResourceServiceImpl(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 获得锁
     *
     * @param resourceKeyPrefix 锁的redis前缀
     * @param resourceId        资源id
     * @param userId            用户id
     * @return 是否获取到
     */
    @Override
    public boolean getLock(String resourceKeyPrefix, int resourceId, int userId) {
        String lock = resourceKeyPrefix + resourceId;
        //如果该userId已经有该项目的锁，锁续期
        String userIdStr = "" + userId;
        if (userIdStr.equals(stringRedisTemplate.opsForValue().get(lock.intern()))) {
            //锁的可重入
            long ttl = stringRedisTemplate.getExpire(lock);
            //续期时间，自己定
            stringRedisTemplate.expire(lock.intern(), ttl + 60L, TimeUnit.SECONDS);
            return true;
        }
        //枷锁
        return stringRedisTemplate.opsForValue()
                .setIfAbsent(lock.intern(), userId + "", 60L, TimeUnit.SECONDS);
    }

    /**
     * 释放锁
     *
     * @param resourceKeyPrefix 锁的redis前缀
     * @param resourceId        资源id
     * @param userId            用户id
     * @return 是否成功
     */
    @Override
    public boolean unLock(String resourceKeyPrefix, int resourceId, int userId) {
        String lock = resourceKeyPrefix + resourceId;
        if ((userId + "").equals(stringRedisTemplate.opsForValue().get(lock.intern()))) {
            stringRedisTemplate.delete(lock.intern());
            return true;
        }
        return false;
    }

    /**
     * 锁延期
     *
     * @param resourceKeyPrefix 锁的redis前缀
     * @param resourceId        资源id
     * @param userId            用户id
     * @return 是否成功
     */
    @Override
    public boolean resetLock(String resourceKeyPrefix, int resourceId, int userId) {
        String lock = resourceKeyPrefix + resourceId;
        //如果该userId已经有该项目的锁，锁续期
        String userIdStr = "" + userId;
        if (userIdStr.equals(stringRedisTemplate.opsForValue().get(lock.intern()))) {
            long ttl = stringRedisTemplate.getExpire(lock);
            stringRedisTemplate.expire(lock.intern(), ttl + 60L, TimeUnit.SECONDS);
            return true;
        }
        return false;
    }
}
