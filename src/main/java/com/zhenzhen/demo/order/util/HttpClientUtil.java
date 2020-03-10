package com.zhenzhen.demo.order.util;

import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.*;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class HttpClientUtil {



	public static ClientHttpRequestFactory createClientHttpRequestFactory() {
		// 长连接保持30秒
		PoolingHttpClientConnectionManager pollingConnectionManager = new PoolingHttpClientConnectionManager(60,
				TimeUnit.SECONDS);
		// 总连接数
		pollingConnectionManager.setMaxTotal(1000);
		// 同路由的并发数
		pollingConnectionManager.setDefaultMaxPerRoute(1000);

		HttpClientBuilder httpClientBuilder = HttpClients.custom();
		httpClientBuilder.setConnectionManager(pollingConnectionManager);
		// 重试次数，默认是3次，没有开启
		httpClientBuilder.setRetryHandler(new DefaultHttpRequestRetryHandler(2, true));
		// 保持长连接配置，需要在头添加Keep-Alive
		httpClientBuilder.setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy());

		List<Header> headers = new ArrayList<>();
		headers.add(new BasicHeader("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.16 Safari/537.36"));
		headers.add(new BasicHeader("Accept-Encoding", "gzip,deflate"));
		headers.add(new BasicHeader("Accept-Language", "zh-CN"));
		headers.add(new BasicHeader("Connection", "Keep-Alive"));
		headers.add(new BasicHeader("Keep-Alive", "120"));

		httpClientBuilder.setDefaultHeaders(headers);

		HttpClient httpClient = httpClientBuilder.build();
		// httpClient连接配置，底层是配置RequestConfig
		HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(
				httpClient);
		// 连接超时
		clientHttpRequestFactory.setConnectTimeout(5000);
		// 数据读取超时时间，即SocketTimeout
		clientHttpRequestFactory.setReadTimeout(5000);
		// 连接不够用的等待时间，不宜过长，必须设置，比如连接不够用时，时间过长将是灾难性的
		clientHttpRequestFactory.setConnectionRequestTimeout(5000);
		// 缓冲请求数据，默认值是true。通过POST或者PUT大量发送数据时，建议将此属性更改为false，以免耗尽内存。
		clientHttpRequestFactory.setBufferRequestBody(true);
		return clientHttpRequestFactory;
	}


	/**
	 * 创建HttpClient
	 *
	 * @param isMultiThread
	 * @return
	 */
	public static HttpClient buildHttpClient(boolean isMultiThread) {
		CloseableHttpClient client;
		if (isMultiThread) {
			client = HttpClientBuilder.create().setConnectionManager(new PoolingHttpClientConnectionManager()).build();
		} else {
			client = HttpClientBuilder.create().build();
		}
		return client;
	}

	/**
	 * 构建httpPost对象
	 *
	 * @param url
	 * @param params
	 * @return
	 */
	public static HttpPost buildFormHttpPost(String url, Map<String, String> params) {
		HttpPost post = new HttpPost(url);
		HttpEntity entity = null;
		if (params != null) {
			List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
			for (String key : params.keySet()) {
				nameValuePair.add(new BasicNameValuePair(key, params.get(key)));
			}
			entity = new UrlEncodedFormEntity(nameValuePair, Consts.UTF_8);
			post.setEntity(entity);
		}
		return post;
	}



}
