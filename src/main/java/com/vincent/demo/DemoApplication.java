package com.vincent.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@EnableCaching
public class DemoApplication {

	@RequestMapping("/index")
	public String index() {
		return "Hello Spring boot";
	}

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
		// 访问 http://localhost:9090/spring-boot/index
	}
}
