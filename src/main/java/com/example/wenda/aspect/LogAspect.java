package com.example.wenda.aspect;


import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Before;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

/**
 * Created by lenovo on 2017/5/24.
 */

@Component
public class LogAspect {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(LogAspect.class);

    @Before("execution(* com.example.wenda.controller.IndexController.*(..))")
    public void beforeMethod(JoinPoint joinPoint){
        StringBuilder sb=new StringBuilder();
        for (Object arg:joinPoint.getArgs()){
            if(arg!=null){
                sb.append("arg"+arg.toString()+"|");
            }
        }

        logger.info("before method");
    }

    @After("execution(* com.example.wenda.controller.IndexController.*(..))")
    public void afterMethod(JoinPoint joinPoint){

    }

}
