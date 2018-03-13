package com.vincent.springTest.aop;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class AopTestMain {
	public static void main(String[] args) {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AopConfig.class);
		// 注解式拦截
		DemoAnnotationService annoService = context.getBean(DemoAnnotationService.class);
		annoService.add();

		// 方法式拦截
		DemoMethodService methodService = context.getBean(DemoMethodService.class);
		methodService.add();
		context.close();

	}
}
