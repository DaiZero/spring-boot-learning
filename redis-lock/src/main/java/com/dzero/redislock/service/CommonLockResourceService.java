package com.dzero.redislock.service;

/**
 * CommonLockResourceService
 *
 * @author DaiZedong
 */
public interface CommonLockResourceService {
    /**
     * 获得锁
     *
     * @param resourceKeyPrefix 锁的redis前缀
     * @param resourceId        资源id
     * @param userId            用户id
     * @return 是否获取到
     */
    boolean getLock(String resourceKeyPrefix, int resourceId, int userId);


    /**
     * 释放锁
     *
     * @param resourceKeyPrefix 锁的redis前缀
     * @param resourceId        资源id
     * @param userId            用户id
     * @return 是否成功
     */
    boolean unLock(String resourceKeyPrefix, int resourceId, int userId);


    /**
     * 锁延期
     *
     * @param resourceKeyPrefix 锁的redis前缀
     * @param resourceId        资源id
     * @param userId            用户id
     * @return 是否成功
     */
    boolean resetLock(String resourceKeyPrefix, int resourceId, int userId);
}
