package com.vincent.springTest.aop;

import java.lang.reflect.Method;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Aspect
@Component
/**
 * 
 * @ClassName: LogAspect
 * @Description: 编写切面
 * @author wensen
 * @date 2018年3月13日 上午10:18:23
 *
 */
public class LogAspect {

	@Pointcut("@annotation(com.vincent.springTest.aop.Action)")
	public void annontationPointCut() {

	}

	@After("annontationPointCut()")
	public void after(JoinPoint joinPoint) {
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		Method method = signature.getMethod();
		Action action = method.getAnnotation(Action.class);
		System.out.println("注解式拦截" + action.myname());
	}
	

	@Before("execution(* com.vincent.springTest.aop.DemoMethodService.*(..) )")
	public void before(JoinPoint joinPoint) {
		System.out.println(" before 执行 DemoAnnotationService方法");
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		Method method = signature.getMethod();
		System.out.println("方法规则式拦截" + method.getName());
	}

}
