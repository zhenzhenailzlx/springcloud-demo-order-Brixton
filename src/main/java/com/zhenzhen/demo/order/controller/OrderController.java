package com.zhenzhen.demo.order.controller;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class OrderController {
	
	@Autowired
	private RestTemplate restTemplate;


	
	@RequestMapping("/ping")
	public String getOrder() {
		 return "tong"+System.currentTimeMillis();
	}
	
	@RequestMapping("/getUserInfoRibbon")
	public String getUserInfoRibbon() {

		return restTemplate.getForObject("http://USER/getUser", String.class);
	}

	@RequestMapping("/getUserSleepRibbon")
	@HystrixCommand(fallbackMethod = "defaultStores")
	public String getUserSleepRibbon(String time) {

		return restTemplate.getForObject("http://USER/sleep?time="+time, String.class);
	}

	public String defaultStores(String time) {
		return "服务发生降级";
	}
	
	
	@RequestMapping("/sleep")
	public String sleep(String time) throws InterruptedException {
		int timeInt = 200;
		if(StringUtils.isNotEmpty(time)) {
			timeInt = Integer.parseInt(time);
		}
		Thread.sleep(timeInt);
		return "sleep"+timeInt;
	}
	
}
