package com.vincent.springTest.main;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.vincent.springTest.config.Config;
import com.vincent.springTest.service.UserFunctionService;

public class Main {

	public static void main(String[] args) {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(Config.class);
		UserFunctionService service = context.getBean(UserFunctionService.class);
		service.sayHello();
		context.close();
	}

}
