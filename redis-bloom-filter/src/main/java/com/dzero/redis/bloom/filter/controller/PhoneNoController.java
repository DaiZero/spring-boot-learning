package com.dzero.redis.bloom.filter.controller;

import com.dzero.redis.bloom.filter.common.BloomFilterHelper;
import com.dzero.redis.bloom.filter.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author dzd
 * @date 2020/9/21 17:26
 */
@RequestMapping("phone")
@RestController
@Slf4j
public class PhoneNoController {

    public static final String PHONE_NO_BLOOM_KEY = "phone_no_bloom";

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private BloomFilterHelper bloomFilter;

    @GetMapping(value = "/batch/add", produces = "application/json")
    public void batchAddToBloom() {
        for (int i = 0; i < 1000000; i++) {
            redisUtil.addByBloomFilter(bloomFilter, PHONE_NO_BLOOM_KEY, String.valueOf(i));
        }
    }

    @GetMapping(value = "/batch/find", produces = "application/json")
    public void batchAddToBloom2() {
        int count = 0;
        for (int i = 1000000; i < 2000000; i++) {
            boolean isExist = redisUtil.includeByBloomFilter(bloomFilter, PHONE_NO_BLOOM_KEY, String.valueOf(i));
            if (isExist) {
                count++;
                log.info("误判：" + i);
            }
        }
        log.info("总共的误判数:" + count);
    }

    @GetMapping(value = "/addNoToBloom", produces = "application/json")
    public void addNoToBloom(String phoneNo) {
        log.info("新增手机号：" + phoneNo);
        redisUtil.addByBloomFilter(bloomFilter, PHONE_NO_BLOOM_KEY, phoneNo);
    }

    @GetMapping(value = "/findNoInBloom", produces = "application/json")
    public Boolean findNoInBloom(String phoneNo) {
        boolean isExist = redisUtil.includeByBloomFilter(bloomFilter, PHONE_NO_BLOOM_KEY, phoneNo);
        log.info("手机号：" + phoneNo + (isExist ? " 存在" : " 不存在"));
        return isExist;
    }
}
