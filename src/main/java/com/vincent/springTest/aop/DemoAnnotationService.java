package com.vincent.springTest.aop;

import org.springframework.stereotype.Service;

@Service
/**
 * @ClassName: DemoAnnotationService
 * @Description: 使用注解的被拦截类
 * @author wensen
 * @date 2018年3月13日 上午10:15:22
 *
 */
public class DemoAnnotationService {

	@Action(myname = "[添加了注解式拦截的add方法]")
	public void add() {

	}
}
