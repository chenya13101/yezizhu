package com.vincent.springTest.aop;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import com.vincent.springTest.service.FunctionService;

@Configuration // 之所以在这里看不到影响，是因为这类没有定义方法与属性
@ComponentScan("com.vincent.springTest.aop")
@EnableAspectJAutoProxy // 开启spring对aspectJ的支持
public class AopConfig {

	//@Configuration 如果之前的bean没有定义 @Service，那么这里会返回一个bean
	@Bean
	public FunctionService functionService() {
		return new FunctionService(); // 这里可以动态指定实现类
	}

}
