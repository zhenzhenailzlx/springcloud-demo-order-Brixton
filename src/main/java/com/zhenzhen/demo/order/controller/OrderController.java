package com.zhenzhen.demo.order.controller;

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
