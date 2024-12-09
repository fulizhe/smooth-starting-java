package com.fulizhe.ssj.sample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import cn.hutool.core.date.StopWatch;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
public class ApplicationWithUndertow {

    public static void main(String[] args) throws Exception {
        final StopWatch stopWatch = new StopWatch();

        stopWatch.start();

        applicationRun(args);// 初始化Spring容器并启动

        stopWatch.stop();
        log.info("###启动耗时: {}s", stopWatch.getTotalTimeSeconds());
    }

    private static void applicationRun(String[] args) {
        final SpringApplication springApplication = new SpringApplication(ApplicationWithUndertow.class);
//        final Boolean humanableStart = false;// Convert.toBool(CommonUtil.getProperty("START_HUMANABLE",
//                                            // "true"));
//        if (humanableStart) {
//            springApplication.setApplicationContextFactory(
//                    new AnnotationConfigServletWebServerApplicationContextWithHumanableStart.Factory());
//            // springApplication.setMainApplicationClass(
//            // AnnotationConfigServletWebServerApplicationContextWithHumanableStart.class);
//            // // 自定义
//        }

        // 启动Spring容器
        springApplication.run(args);
    }
}