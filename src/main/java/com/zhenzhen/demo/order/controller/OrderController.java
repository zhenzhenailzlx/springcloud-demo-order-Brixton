package com.zhenzhen.demo.order.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.FutureRequestExecutionService;
import org.apache.http.impl.client.HttpRequestFutureTask;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.zhenzhen.demo.order.service.OrderService;
import com.zhenzhen.demo.order.util.HttpConnectionManager4;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class OrderController {
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private OrderService orderService;

	
	@RequestMapping("/getOrder")
	public Map<String,Object> getOrder() {
		log.info("I am in getOrder");
		 Map<String,Object> userMap = new HashMap<String,Object>();
		 userMap.put("id", "1");
		 userMap.put("name", "银真");
		 userMap.put("age", "31");
		 return userMap;
	}
	
	@RequestMapping("/getUserInfoRibbon")
	public String getUserInfoRibbon() {
		log.info("I am in getUserInfoRibbon");
		return restTemplate.getForObject("http://USER/getUser", String.class);
	}
	
	@RequestMapping("/getUserInfoFutureRequest")
	public String getUserInfoFutureRequest() throws Exception {
		return orderService.getUserInfoFutureRequest();
	}
	
	
	
	
	private HttpUriRequest getMethod() {
		String code = "wgec";
		int type = 1;
		String url = "http://10.20.9.118:8000/getUser";
		String queryString = "";// json/xml使用
		List<NameValuePair> data = null;// 键值对使用
		int reqDataType = 1;// 参数类型

		if (type == 1) {
			// get方式
			HttpGet method = new HttpGet(url);
			String str = "";
			try {
				method.setURI(new URI(method.getURI().toString() + str));
				try {
					// 构建请求头
					method = (HttpGet) buildRequestHead(code, method, null);
				} catch (Exception e) {
					log.error("构建HttpGet方法时设置请求头或请求参数出错！" + e.getMessage());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return method;
		} else if (type == 2) {
			// Post方式
			HttpPost method = new HttpPost(url);

			try {
				// 构建请求头
				method = (HttpPost) buildRequestHead(code, method, null);
			} catch (Exception e) {
				log.error("构建HttpPost方法时设置请求头或请求参数出错！" + e.getMessage());
			}

			if (reqDataType == 1) {// 参数为键值对
				try {
					method.setEntity(new UrlEncodedFormEntity(data, "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					log.error("设置HttpPost参数时出错！" + e.getMessage());
				}
			} else if (reqDataType == 2) {// 参数为json
				StringEntity entity = new StringEntity(queryString, "utf-8");// 解决中文乱码问题
				method.setEntity(entity);
			} 
			return method;
		}
		return null;
	}
	
	// 构建头部
	private HttpUriRequest buildRequestHead(String code, HttpUriRequest method, Map<String, String> headMap) throws Exception {
        for (Map.Entry<String, String> entry : headMap.entrySet()) {
            method.addHeader(entry.getKey(), entry.getValue());
        }
		return method;
	}

	@RequestMapping("/getOrderPressLog")
	public String getOrderPressLog() throws InterruptedException {
		log.info("带日志压力测试开始");
		Thread.sleep(200);
		log.info("带日志压力测试结束");
		return "压力测试";
	}
	
	@RequestMapping("/getOrderPressSleep200ms")
	public String getOrderPressSleep200ms() throws InterruptedException {
		Thread.sleep(200);
		return "压力测试";
	}
	
}
