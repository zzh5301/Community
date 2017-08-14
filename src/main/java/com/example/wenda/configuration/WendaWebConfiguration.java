package com.example.wenda.configuration;


import com.example.wenda.interceptor.LoginRequiredInterceptor;
import com.example.wenda.interceptor.PassportInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Created by lenovo on 2017/5/25.
 */
public class WendaWebConfiguration  extends WebMvcConfigurerAdapter{

    @Autowired
    PassportInterceptor passportInterceptor;

    @Autowired
    LoginRequiredInterceptor loginRequiredInterceptor;


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(passportInterceptor);
        registry.addInterceptor(loginRequiredInterceptor);
        super.addInterceptors(registry);
    }
}
