package com.example.wenda.async;

import com.alibaba.fastjson.JSON;
import com.example.wenda.util.JedisAdapter;
import com.example.wenda.util.RedisKeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by lenovo on 2017/5/28.
 */

@Service
public class EventConsumer implements InitializingBean ,ApplicationContextAware {

    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);

    private Map<EventType,List<EventHandler>> config=new HashMap<>();
    private ApplicationContext applicationContext;



    @Autowired
    JedisAdapter jedisAdapter;


    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String,EventHandler> beans=applicationContext.getBeansOfType(EventHandler.class);
        if(beans!=null){
            for(Map.Entry<String,EventHandler> entry:beans.entrySet()){
                List<EventType> eventTypes=entry.getValue().getSupportEventTypes();
                for(EventType type:eventTypes){
                    if(!config.containsKey(type)){
                        config.put(type,new ArrayList<EventHandler>());
                    }
                    config.get(type).add(entry.getValue());
                }
            }
        }

        ExecutorService service= Executors.newFixedThreadPool(4);

        service.submit(new Runnable() {
            @Override
            public void run() {
                while(true){
                    String key= RedisKeyUtil.getEventQueueKey();
                    List<String> events=jedisAdapter.brpop(0,key);
                    if(events!=null){
                        for(String message:events){
                            if(message.equals(key)){
                                continue;
                            }
                            EventModel eventModel= JSON.parseObject(message,EventModel.class);
                            if(!config.containsKey(eventModel.getType())){
                                logger.error("不能识别事件");
                                continue;
                            }

                            for(EventHandler eventHandler:config.get(eventModel.getType())){
                                eventHandler.doHandle(eventModel);
                            }
                        }
                    }
                }
            }
        });

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext=applicationContext;
    }
}
