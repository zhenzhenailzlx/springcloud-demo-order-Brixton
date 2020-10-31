package com.zhenzhen.demo.order.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
@Slf4j
public class ControllerMethodExecutionLogAspect {
	
	private static final String START_TIME = "requestStartTime";

	@Pointcut("execution(public * com.zhenzhen.demo.*.controller.*.*(..))")
	public void log() {
	}
	
	@Before("log()")
	public void doStart(JoinPoint joinpoint) {
		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		HttpServletRequest req = attributes.getRequest();
		long start = System.currentTimeMillis();
		req.setAttribute(START_TIME, start);
		log.info("********request start,url = {}",req.getRequestURL().toString());
		log.info("Method = {}",req.getMethod());
		log.info("ip = {}",req.getRemoteAddr());
		log.info("class_method = {}",joinpoint.getSignature().getDeclaringTypeName()+"."+joinpoint.getSignature().getName());
		log.info("args = {}",joinpoint.getArgs());
		
	}
	
	@AfterReturning(returning = "object",pointcut="log()")
	public void doEnd(Object object) {
        log.info("args = {}",object.toString());
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest req = attributes.getRequest();
        String url = req.getRequestURL().toString();
		long start = (Long) req.getAttribute(START_TIME);
        long end = System.currentTimeMillis();
        log.info("********request completed. url:{}, cost:{}", url, end - start);
	}

}
