1.启动eureka
java -jar eureka.jar 

java -jar eureka.jar --spring.profiles.active=peer1
java -jar eureka.jar --spring.profiles.active=peer2

java -jar eureka.jar --spring.profiles.active=eureka0
java -jar eureka.jar --spring.profiles.active=eureka1
java -jar eureka.jar --spring.profiles.active=eureka2

访问eureka 
http://localhost:8761


2.启动user
java  -jar user.jar
java  -jar user.jar --server.port=8000         

访问user
http://localhost:8010/getUser

http://localhost:8010/ping

http://localhost:8010/sleep?time=5000


压测user
ab -n 10000 -c 50  http://localhost:8010/sleep?time=10


3.启动order服务
java -jar  order.jar

3.1访问order 
http://localhost:8011/getUserInfoRibbon


ribbon实验1:ribbon设置10s，httpclient设置5秒，看那个生效

ribbon:
  ConnectTimeout: 10000
  ReadTimeout: 10000

HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(
		httpClient);
// 连接超时
clientHttpRequestFactory.setConnectTimeout(5000);
// 数据读取超时时间，即SocketTimeout
clientHttpRequestFactory.setReadTimeout(5000);

http://localhost:8011/getUserSleepRibbon?time=6000访问失败，说明ribbon配置没有生效


ribbon实验2:ribbon设置10s，httpclient设置5秒，看那个生效

ribbon:
  ConnectTimeout: 5000
  ReadTimeout: 5000

HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(
		httpClient);
// 连接超时
clientHttpRequestFactory.setConnectTimeout(10000);
// 数据读取超时时间，即SocketTimeout
clientHttpRequestFactory.setReadTimeout(10000);

http://localhost:8011/getUserSleepRibbon?time=6000 可以访问成功，说明ribbon配置失效

ribbon实验3:ribbon设置5s，httpclient不设置，看那个生效
ribbon:
  ConnectTimeout: 5000
  ReadTimeout: 5000

HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(
		httpClient);
// 连接超时
//clientHttpRequestFactory.setConnectTimeout(10000);
// 数据读取超时时间，即SocketTimeout
//clientHttpRequestFactory.setReadTimeout(10000);

http://localhost:8011/getUserSleepRibbon?time=6000 可以访问成功，说明ribbon配置的ReadTimeout无效


实验1实验2和实验3 说明使用了ribbon调用的化，这个参数不起作用


触发降级
1.宕机
2.异常
3.超时


实验4 
hystrix.command.default.execution.isolation.thread.timeoutInMilliseconds: 15000

HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(
		httpClient);
// 连接超时
clientHttpRequestFactory.setConnectTimeout(5000);
// 数据读取超时时间，即SocketTimeout
clientHttpRequestFactory.setReadTimeout(5000);

http://localhost:8011/getUserSleepRibbon?time=6000  服务发生降级


实验5 
hystrix.command.default.execution.isolation.thread.timeoutInMilliseconds: 5000

HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(
		httpClient);
// 连接超时
clientHttpRequestFactory.setConnectTimeout(10000);
// 数据读取超时时间，即SocketTimeout
clientHttpRequestFactory.setReadTimeout(10000);

http://localhost:8011/getUserSleepRibbon?time=6000  服务发生降级

实验4和实验5说明  timeoutInMilliseconds和setReadTimeout 谁小使用谁


实验6 异常降级

实验7 宕机降级


