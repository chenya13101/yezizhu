package com.vincent.springTest.el;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class ElMain {

	public static void main(String[] args) {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ElConfig.class);
		ElConfig config = context.getBean(ElConfig.class);
		config.outputResource();
		context.close();
	}
}
