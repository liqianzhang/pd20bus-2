package com.practice.controller;

import com.practice.entity.User;
import com.practice.mapper.UserMapper;
import com.practice.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @MethodName: $
 * @Description: TODO
 * @Param: $
 * @Return: $
 * @Author: zhangliqian
 * @Date: $
 */
@RestController
@Slf4j
public class FunctionIntegrationTestController {
    @Autowired
    UserMapper userMapper;

    /**
     * redis 连通性测试
     * @param key
     * @param value
     * @param type
     * @return
     */
    @PostMapping("redis/test1")
    public String test1(@RequestParam("key") String key, @RequestParam("value") String value, @RequestParam("type") String type) {
        log.info("==>FunctionIntegrationTestController.test1, key={},value={},type={}",key, value, type);
        String val = null;
        try {
            RedisUtil.set(key, value);
            val = RedisUtil.get(key).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("==> val={}", val);

        return null;
    }

    /**
     * mybatis连通性测试
     * @return
     */
    @PostMapping("mybatis/select")
    public List testMybatis() {
        log.info("==>FunctionIntegrationTestController.testMybatis");
        List<User> users = null;
        try {
            users = userMapper.selectAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }
}
