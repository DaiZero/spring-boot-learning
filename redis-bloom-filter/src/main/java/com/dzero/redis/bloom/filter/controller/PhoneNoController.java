package com.dzero.redis.bloom.filter.controller;

import com.dzero.redis.bloom.filter.common.BloomFilterHelper;
import com.dzero.redis.bloom.filter.util.RedisUtil;
import com.google.common.base.Charsets;
import com.google.common.hash.Funnel;
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
    private  BloomFilterHelper bloomFilter;

    @GetMapping(value = "/addNoToBloom", produces = "application/json")
    public void addNoToBloom(String phoneNo) {
        redisUtil.addByBloomFilter(bloomFilter, PHONE_NO_BLOOM_KEY, phoneNo);
    }

    @GetMapping(value = "/findNoInBloom", produces = "application/json")
    public Boolean findNoInBloom(String phoneNo) {
        boolean isExist = redisUtil.includeByBloomFilter(bloomFilter, PHONE_NO_BLOOM_KEY, phoneNo);
        return isExist;
    }
}
