package com.vincent.springTest.aop;

import java.lang.annotation.Target;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
/**
 * 
 * @ClassName: Action
 * @Description: 定义一个拦截规则
 * @author wensen
 * @date 2018年3月13日 上午10:03:44
 */
public @interface Action {

	String myname();
}
