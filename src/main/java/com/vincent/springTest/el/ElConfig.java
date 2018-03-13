package com.vincent.springTest.el;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;

@Configuration
@ComponentScan("com.vincent.springTest.el")
@PropertySource("classpath:com/vincent/springTest/el/test.properties")  //引入properties配置文件
public class ElConfig {

	@Value("#{systemProperties['os.name']}")
	private String osName;

	@Value("#{ T(java.lang.Math).random() * 100.0 }")
	private double randomNumber;

	@Value("#{elServiceDemo1.another}")
	private String fromAnother;

	@Value("classpath:com/vincent/springTest/el/test.txt")
	private Resource testFile;

	@Value("http://www.baidu.com")
	private Resource testUtl;

	@Value("${book.name}")
	private String bookName;

	@Autowired
	private Environment environment;

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertyConfigure() {
		return new PropertySourcesPlaceholderConfigurer();
	}

	public void outputResource() {

		System.out.println(osName);
		System.out.println(randomNumber);
		System.out.println(fromAnother);
		System.out.println(bookName);
		try {
			System.out.println(IOUtils.toString(testFile.getInputStream()));
			System.out.println(IOUtils.toString(testUtl.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println(environment.getProperty("book.author"));

	}

}
