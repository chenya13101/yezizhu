package com.vincent.demo.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vincent.demo.component.settings.AuthorSettings;

/**
 * 
 * 常规属性配置 application.properties用{@value}直接读取属性
 * 
 * @ClassName: BookRest
 * @author wensen
 * @date 2018年3月29日 上午11:26:11
 *
 */
@RestController
@RequestMapping("book")
public class BookRest {
	@Value("${book.name}")
	private String bookName;

	// 在spring boot 可以直接访问application.properties文件定义的属性
	// 常规spring环境下，需要用@PropertySource指明文件位置
	@Value("${book.author}")
	private String bookAuthor;

	@Autowired
	private AuthorSettings authorSettings;

	@RequestMapping("/getBookInfo")
	public String getBookInfo() {
		// url = http://localhost:9090/spring-boot/book/getBookInfo
		String s = bookAuthor + " \n " + bookName;
		return s;
	}

	@RequestMapping("/getAuthorInfo")
	public String getAuthorInfo() {
		// url = http://localhost:9090/spring-boot/book/getAuthorInfo
		String s = authorSettings.getAge() + " \n " + authorSettings.getCity();
		return s;
	}
}
