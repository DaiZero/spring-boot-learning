package com.dzero.mp.template;

import com.dzero.mp.template.entity.User;
import com.dzero.mp.template.mapper.UserMapper;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import java.util.List;

@SpringBootTest
class MybatisPlusTemplateApplicationTests {

	@Test
	void contextLoads() {
	}

	@Autowired
	private UserMapper userMapper;

	@Test
	void getList(){
		List<User> users = userMapper.selectList(null);
		Assert.assertEquals(5, users.size());
		users.forEach(System.out::println);
	}

}
