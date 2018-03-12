package com.vincent.springTest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserFunctionService {

	@Autowired
	FunctionService functionService;

	public void sayHello() {
		functionService.sayHello();
	}
}
