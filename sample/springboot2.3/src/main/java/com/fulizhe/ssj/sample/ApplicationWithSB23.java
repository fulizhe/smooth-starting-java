package com.fulizhe.ssj.sample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.fulizhe.ssj.AnnotationConfigServletWebServerApplicationContextWithHumanableStart;

import cn.hutool.core.date.StopWatch;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
public class ApplicationWithSB23 {

    public static void main(String[] args) throws Exception {
        final StopWatch stopWatch = new StopWatch();

        stopWatch.start();

        applicationRun(args);// 初始化Spring容器并启动

        stopWatch.stop();
        log.info("###启动耗时: {}s", stopWatch.getTotalTimeSeconds());
    }

    private static void applicationRun(String[] args) {
        final SpringApplication springApplication = new SpringApplication(ApplicationWithSB23.class);
        springApplication.setApplicationContextClass(
                AnnotationConfigServletWebServerApplicationContextWithHumanableStart.class);
        // 启动Spring容器
        springApplication.run(args);
    }
}