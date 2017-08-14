package com.example.wenda.controller;

import com.example.wenda.async.EventModel;
import com.example.wenda.async.EventProducer;
import com.example.wenda.async.EventType;
import com.example.wenda.model.Comment;
import com.example.wenda.model.EntityType;
import com.example.wenda.model.HostHolder;
import com.example.wenda.service.CommentService;
import com.example.wenda.service.LikeService;
import com.example.wenda.util.WendaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by lenovo on 2017/5/28.
 */

@Controller
public class LikeController {

    @Autowired
    LikeService likeService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    CommentService commentService;

    @Autowired
    EventProducer eventProducer;

    @RequestMapping(path = {"/like"},method = {RequestMethod.POST})
    @ResponseBody
    public String like(@RequestParam("commentId") int commentId){
        if(hostHolder.getUser()==null){
            return WendaUtil.getJSONString(999);
        }
        Comment comment=commentService.getCommentById(commentId);

        eventProducer.fireEvent(new EventModel(EventType.LIKE).setActorId(hostHolder.getUser().getId()).setEntityType(EntityType.ENTITY_COMMENT).setEntityId(commentId).setEntityOwnerId(comment.getUserId()).setExt("questionId",String.valueOf(comment.getEntityId())));

        long likeCount=likeService.like(hostHolder.getUser().getId(),EntityType.ENTITY_COMMENT,commentId);


        return  WendaUtil.getJSONString(0,String.valueOf(likeCount));
    }


    @RequestMapping(path = {"/dislike"}, method = {RequestMethod.POST})
    @ResponseBody
    public String dislike(@RequestParam("commentId") int commentId) {
        if (hostHolder.getUser() == null) {
            return WendaUtil.getJSONString(999);
        }

        long likeCount = likeService.disLike(hostHolder.getUser().getId(), EntityType.ENTITY_COMMENT, commentId);
        return WendaUtil.getJSONString(0, String.valueOf(likeCount));
    }


}
