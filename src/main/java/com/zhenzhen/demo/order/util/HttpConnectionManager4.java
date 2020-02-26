package com.zhenzhen.demo.order.util;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class HttpConnectionManager4 {
	private static final Logger log = LoggerFactory.getLogger(HttpConnectionManager4.class);
	static final int maxTotal = 2048; // 最大连接数
	static final int maxPerRoute = 256;// 每个路由最大连接数
	private final static Object syncLock = new Object();
	private final static ConcurrentMap<String, PoolingHttpClientConnectionManager> map = new ConcurrentHashMap<String, PoolingHttpClientConnectionManager>();
	private final static ConcurrentMap<String, CloseableHttpClient> clientmap = new ConcurrentHashMap<String, CloseableHttpClient>();
	private static PoolingHttpClientConnectionManager cm;// 创建连接池
	private static CloseableHttpClient httpClient;

	private static PoolingHttpClientConnectionManager getPool(String code) {
		if (map.get(code) == null) {
			synchronized (syncLock) {
				if (map.get(code) == null) {
					try {
                        HostnameVerifier hostnameVerifier =
                                SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
                        SSLConnectionSocketFactory sslsf =
                                new SSLConnectionSocketFactory(createIgnoreVerifySSL(),
                                        hostnameVerifier);

						Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory> create()
											.register("http", PlainConnectionSocketFactory.INSTANCE)
											.register("https", sslsf).build();

						cm = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
						cm.setMaxTotal(maxTotal);
						if(code.startsWith("wgec")){
							cm.setDefaultMaxPerRoute(maxPerRoute*2);
						}else{
							cm.setDefaultMaxPerRoute(maxPerRoute);
						}
						map.put(code, cm);
						log.debug(code + "接口:PoolingNHttpClientConnectionManager请求池初始化……");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		return map.get(code);
	}

	/** 
	 * 绕过验证 
	 *   
	 * @return 
	 * @throws NoSuchAlgorithmException  
	 * @throws KeyManagementException  
	 */
	public static SSLContext createIgnoreVerifySSL() throws NoSuchAlgorithmException, KeyManagementException {
		SSLContext sc = SSLContext.getInstance("SSLv3");

		// 实现一个X509TrustManager接口，用于绕过验证，不用修改里面的方法
		X509TrustManager trustManager = getX509TrustManager();

		sc.init(null, new TrustManager[]{trustManager}, null);
		return sc;
	}

	private static X509TrustManager getX509TrustManager() {
		return new X509TrustManager() {
			@Override
			public void checkClientTrusted(
					X509Certificate[] paramArrayOfX509Certificate,
					String paramString) throws CertificateException {
			}

			@Override
			public void checkServerTrusted(
					X509Certificate[] paramArrayOfX509Certificate,
					String paramString) throws CertificateException {
			}

			@Override
			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}
		};
	}

	public static CloseableHttpClient getHttpClient() {
		String timeType = "5";// 超时类型
		String code = "wgec";// 接口code
		String mapKey = code + "_" + timeType;
		if (clientmap.get(mapKey) == null) {
			synchronized (syncLock) {
				if (clientmap.get(code) == null) {
					RequestConfig requestConfig = null;
                    if ("5" == timeType || "5".equals(timeType)) {
                        requestConfig = RequestConfig.custom().setConnectTimeout(2000).setConnectionRequestTimeout(1000).setSocketTimeout(4000).setCookieSpec(CookieSpecs.BEST_MATCH).build();//
                        log.info("接口类型：4s");
                    }else if ("4" == timeType || "4".equals(timeType)) {
						requestConfig = RequestConfig.custom().setConnectTimeout(2000).setConnectionRequestTimeout(1000).setSocketTimeout(20000).setCookieSpec(CookieSpecs.BEST_MATCH).build();//
                        log.info("接口类型：超级慢");
					} else if ("3" == timeType || "3".equals(timeType)) {
						requestConfig = RequestConfig.custom().setConnectTimeout(2000).setConnectionRequestTimeout(1000).setSocketTimeout(10000).setCookieSpec(CookieSpecs.BEST_MATCH).build();//
						log.info("接口类型：慢");
					} else if ("2" == timeType || "2".equals(timeType)) {
						requestConfig = RequestConfig.custom().setConnectTimeout(2000).setConnectionRequestTimeout(1000).setSocketTimeout(5000).setCookieSpec(CookieSpecs.BEST_MATCH).build();//
						log.info("接口类型：中");
					} else if ("1" == timeType || "1".equals(timeType)) {
						requestConfig = RequestConfig.custom().setConnectTimeout(2000).setConnectionRequestTimeout(1000).setSocketTimeout(3000).setCookieSpec(CookieSpecs.BEST_MATCH).build();//
						log.info("接口类型：快");
					} else if ("0" == timeType || "0".equals(timeType)) {
						requestConfig = RequestConfig.custom().setConnectTimeout(1000).setConnectionRequestTimeout(1000).setSocketTimeout(1000).setCookieSpec(CookieSpecs.BEST_MATCH).build();//
						log.info("接口类型：特别快");
                    } else if ("-1" == timeType || "-1".equals(timeType)) {
                        requestConfig = RequestConfig.custom().setConnectTimeout(1).setConnectionRequestTimeout(1).setSocketTimeout(1).setCookieSpec(CookieSpecs.BEST_MATCH).build();//
                        log.info("测试报警");
                    }

					// 请求重试处理
					HttpRequestRetryHandler httpRequestRetryHandler = new HttpRequestRetryHandler() {
						public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
							if (executionCount >= 3) {// 如果已经重试了5次，就放弃
								return false;
							}
							if (exception instanceof org.apache.http.NoHttpResponseException) {// 如果服务器丢掉了连接，那么就重试
								return true;
							}
							if (exception instanceof SSLHandshakeException) {// 不要重试SSL握手异常
								return false;
							}
							if (exception instanceof InterruptedIOException) {// 超时
								return false;
							}
							if (exception instanceof UnknownHostException) {// 目标服务器不可达
								return false;
							}
							if (exception instanceof ConnectTimeoutException) {// 连接被拒绝
								return false;
							}
							if (exception instanceof SSLException) {// SSL握手异常
								return false;
							}

							HttpClientContext clientContext = HttpClientContext.adapt(context);
							HttpRequest request = clientContext.getRequest();
							// 如果请求是幂等的，就再次尝试
							if (!(request instanceof HttpEntityEnclosingRequest)) {
								return true;
							}
							return false;
						}
					};

					// 声明重定向策略对象
					LaxRedirectStrategy redirectStrategy = new LaxRedirectStrategy();
					PoolingHttpClientConnectionManager connManager = getPool(code);
					HttpClientBuilder clientBuilder = HttpClients.custom().setConnectionManager(connManager).setDefaultRequestConfig(requestConfig);
                    // 清除失效链接
					clientBuilder.evictExpiredConnections().evictIdleConnections(30, TimeUnit.SECONDS);
					httpClient = clientBuilder.setRedirectStrategy(redirectStrategy).setRetryHandler(httpRequestRetryHandler).build();
					clientmap.put(mapKey, httpClient);
					log.debug(code + "接口【" + timeType + "】等级速度:CloseableHttpClient客户端初始化……");
				}
			}
		}
		return clientmap.get(mapKey);
	}

}
