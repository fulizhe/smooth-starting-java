package com.fulizhe.ssj.sample;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.RandomUtil;

@RestController
public class HelloController {
    
    public HelloController(){
        ThreadUtil.safeSleep(1000 * 10);
    }

	@GetMapping("/hello")
	public String index(HttpServletRequest request) {
		String randomString = RandomUtil.randomString(5);
		LoggerFactory.getLogger(this.getClass()).info("接受到参数 {}", "name = " + randomString);

		return "hello-jetty";
	}

	@GetMapping("/hello2")
	public String helloFail(HttpServletRequest request) {
		String randomString = RandomUtil.randomString(5);
		LoggerFactory.getLogger(this.getClass()).info("接受到参数 {}", "name = " + randomString);

		throw new RuntimeException("抛出一异常， 验证flow chart");
	}
	
	@RequestMapping(value = "/", produces = "text/html")
	public String home() {
		String appName = "LQ-appName";
		return "<head><title>Sidecar</title></head><body>\n"
				+ "<a href='/ping'>ping</a><br/>\n"
				+ "<a href='/actuator/health'>health</a><br/>\n" + "<a href='/hosts/"
				+ appName + "'>hosts/" + appName + "</a><br/>\n" + "</body>";
	}		

}