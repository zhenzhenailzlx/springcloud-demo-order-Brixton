package com.zhenzhen.demo.order.service.impl;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.FutureRequestExecutionService;
import org.apache.http.impl.client.HttpRequestFutureTask;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.zhenzhen.demo.order.service.OrderService;
import com.zhenzhen.demo.order.util.HttpConnectionManager4;

@Service
public class OrderServiceImpl implements OrderService{

	@Override
	@SentinelResource(value = "getUserInfoFutureRequest")
	public String getUserInfoFutureRequest() throws Exception {
		String resutl = "{}";
		// 构建HttpMethod
		String url = "http://10.20.9.118:8000/getUser";
		HttpGet method = new HttpGet(url);
		FutureRequestExecutionService futureRequestExecutionService = HttpConnectionManager4.getFutureRequestExecutionService();
		
		HttpRequestFutureTask<String> task = futureRequestExecutionService.execute(method, HttpClientContext.create(),
				new ResponseHandler<String>() {

					@Override
					public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
						if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
							HttpEntity entity = response.getEntity();// 调用getEntity()方法获取到一个HttpEntity实例
							return EntityUtils.toString(entity, "utf-8");

						}
						return ""; 
					}
				});
		resutl =  task.get(5, TimeUnit.SECONDS);		
		return resutl;
	}

}
