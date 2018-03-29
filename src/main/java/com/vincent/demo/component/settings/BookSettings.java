package com.vincent.demo.component.settings;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * ConfigurationProperties 默认读取的是application.properties文件内的属性
 * 
 * @ClassName: BookSettings
 * @author wensen
 * @date 2018年3月29日 下午1:54:14
 *
 */
@Component
@ConfigurationProperties(prefix = "book")
// 如果是配置在application.properties里不需要额外加属性locations
public class BookSettings {

	private String name;

	private String author;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

}
