package com.dzero.redis.bloom.filter.controller;

import com.dzero.redis.bloom.filter.common.BloomFilterHelper;
import com.dzero.redis.bloom.filter.util.RedisUtil;
import com.google.common.base.Charsets;
import com.google.common.hash.Funnel;
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
public class PhoneNoController {

    public static final String PHONE_NO_BLOOM_KEY = "phone_no_bloom";

    @Autowired
    private RedisUtil redisUtil;

    @GetMapping(value = "/addNoToBloom", produces = "application/json")
    public void addNoToBloom(String phoneNo) {
        BloomFilterHelper<String> myBloomFilterHelper = new BloomFilterHelper<>((Funnel<String>) (from, into) ->
                into.putString(from, Charsets.UTF_8)
                        .putString(from, Charsets.UTF_8), 1500000, 0.00001);
        redisUtil.addByBloomFilter(myBloomFilterHelper, PHONE_NO_BLOOM_KEY, phoneNo);
    }

    @GetMapping(value = "/findNoInBloom", produces = "application/json")
    public Boolean findNoInBloom(String phoneNo) {
        BloomFilterHelper<String> myBloomFilterHelper = new BloomFilterHelper<>(
                (Funnel<String>) (from, into) ->
                        into.putString(from, Charsets.UTF_8)
                                .putString(from, Charsets.UTF_8)
                , 1500000, 0.00001);
        boolean isExist = redisUtil.includeByBloomFilter(myBloomFilterHelper, PHONE_NO_BLOOM_KEY,
                phoneNo);
        return isExist;
    }
}
