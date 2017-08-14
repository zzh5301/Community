package com.example.wenda.async.handler;

import com.alibaba.fastjson.JSONObject;
import com.example.wenda.async.EventHandler;
import com.example.wenda.async.EventModel;
import com.example.wenda.async.EventType;
import com.example.wenda.model.EntityType;
import com.example.wenda.model.Feed;
import com.example.wenda.model.Question;
import com.example.wenda.model.User;
import com.example.wenda.service.FeedService;
import com.example.wenda.service.FollowService;
import com.example.wenda.service.QuestionService;
import com.example.wenda.service.UserService;
import com.example.wenda.util.JedisAdapter;
import com.example.wenda.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by lenovo on 2017/5/29.
 */

@Component
public class FeedHandler implements EventHandler{

    @Autowired
    FollowService followService;

    @Autowired
    UserService userService;

    @Autowired
    FeedService feedService;


    @Autowired
    JedisAdapter jedisAdapter;

    @Autowired
    QuestionService questionService;


    private String buildFeedDate(EventModel eventModel){

        Map<String,String> map=new HashMap<>();

        User actor=userService.getUser(eventModel.getActorId());
        if(actor==null)
            return null;
        map.put("userId",String.valueOf(actor.getId()));
        map.put("userHead",actor.getHeadUrl());
        map.put("userName",actor.getName());

        if (eventModel.getType() == EventType.COMMENT ||
                (eventModel.getType() == EventType.FOLLOW  && eventModel.getEntityType() == EntityType.ENTITY_QUESTION)) {
            Question question = questionService.getById(eventModel.getEntityId());
            if (question == null) {
                return null;
            }
            map.put("questionId", String.valueOf(question.getId()));
            map.put("questionTitle", question.getTitle());
            return JSONObject.toJSONString(map);
        }
        return null;
    }


    @Override
    public void doHandle(EventModel eventModel) {

        Feed feed=new Feed();
        feed.setCreatedDate(new Date());
        feed.setType(eventModel.getType().getValue());
        feed.setUserId(eventModel.getActorId());
        feed.setData(buildFeedDate(eventModel));
        if(feed.getData()==null)
            return;
        feedService.addFeed(feed);

        List<Integer> followers=followService.getFollowers(EntityType.ENTITY_USER,eventModel.getActorId(),Integer.MAX_VALUE);
        followers.add(0);

        for(int follower:followers){
            String timelineKey= RedisKeyUtil.getTimelineKey(follower);
            jedisAdapter.lpush(timelineKey,String.valueOf(feed.getId()));
        }

    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(new EventType[]{EventType.COMMENT, EventType.FOLLOW});
    }
}
