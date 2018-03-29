package com.vincent.demo.component.settings;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * 
 * @ClassName: AuthorSettings
 * @Description: 配置类，关联一个指定properties文件，指定prefix，自动设置属性
 * @author wensen
 * @date 2018年3月29日 下午1:51:34
 *
 */
@Component
@ConfigurationProperties(prefix = "author")
@PropertySource("classpath:config/author.properties")
// 如果是配置在application.properties里不需要额外加属性locations
public class AuthorSettings {

	private Integer age;

	private String city;

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

}
