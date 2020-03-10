# springcloud-demo-order-Brixton

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
java  -jar user.jar --server.port=8000   -Dcsp.sentinel.dashboard.server=localhost:8080 -Dproject.name=user       

访问user
http://localhost:8000/getUser


压测user
ab -n 10000 -c 50  http://localhost:8000/getUser

ab -n 10000 -c 50  http://localhost:8000/getUserPressSleep200ms



3.启动order服务
java  -jar  order.jar   --server.port=8001   -Dcsp.sentinel.dashboard.server=localhost:8080 -Dproject.name=order      
3.1访问order 
http://localhost:8001/getUserInfoFutureRequest
http://localhost:8001/getUserInfoRibbon



4.sentinel-dashboard启动
java -jar sentinel-dashboard.jar -Dserver.port=8080 -Dcsp.sentinel.dashboard.server=localhost:8080 -Dproject.name=sentinel-dashboard 
访问