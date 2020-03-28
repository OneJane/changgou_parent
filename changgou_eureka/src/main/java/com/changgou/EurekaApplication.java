package com.changgou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * @program: changgou_parent
 * @description:
 * @author: OneJane
 * @create: 2020-03-28 15:47
 **/
@SpringBootApplication
@EnableEurekaServer
public class EurekaApplication {

    /**
     * 加载启动类，以启动类为当前springboot的配置标准
     * http://localhost:7001/ 启动后访问eureka控制台
     * @param args
     */
    public static void main(String[] args) {
        SpringApplication.run(EurekaApplication.class);
    }
}