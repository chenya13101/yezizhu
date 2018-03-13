package com.vincent.springTest.el;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ElServiceDemo1 {

	@Value("其它类型的属性")    //此处为注入普通字符
	private String another;

	public String getAnother() {
		return another;
	}

	public void setAnother(String another) {
		this.another = another;
	}

}
