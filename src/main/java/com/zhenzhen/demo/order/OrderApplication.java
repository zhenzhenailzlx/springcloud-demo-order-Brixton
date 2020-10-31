package com.zhenzhen.demo.order;

import com.zhenzhen.demo.order.util.HttpClientUtil;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringCloudApplication
@EnableEurekaClient
public class OrderApplication {
	
	@Bean
    @LoadBalanced
    RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate(HttpClientUtil.createClientHttpRequestFactory());


        return restTemplate;
    }

	public static void main(String[] args) {
		 new SpringApplicationBuilder(OrderApplication.class).web(true).run(args);
	}
	


}
