package com.example.wenda.controller;

import com.example.wenda.model.HostHolder;
import com.example.wenda.model.Question;
import com.example.wenda.model.User;
import com.example.wenda.model.ViewObject;
import com.example.wenda.service.QuestionService;
import com.example.wenda.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.swing.text.View;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2017/5/25.
 */

@Controller
public class HomeController {
    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    QuestionService questionService;

    @Autowired
    UserService userService;

    @Autowired
    HostHolder hostHolder;



    private List<ViewObject> getQuestions(int userId,int offset,int limit){

        List<Question> questionsList=questionService.getLatestQuestions(userId,offset,limit);
        List<ViewObject> vos=new ArrayList<>();
        for (Question question:questionsList){
            ViewObject vo=new ViewObject();
            vo.set("question",question);
            vo.set("followCount",followService.getFollowerCount());
            vo.set("user",userService.getUser(question.getUserId()));
            vos.add(vo);
        }
        return  vos;
    }

    @RequestMapping(path = {"/","/index"},method = {RequestMethod.GET,RequestMethod.POST})
    public String index(Model model, @RequestParam(value="pop",defaultValue = "0") int pop){

        model.addAttribute("vos",getQuestions(0,0,10));
        return "index";
    }

    @RequestMapping(path = {"/user/{userId}"},method = {RequestMethod.GET,RequestMethod.POST})
    public String userIndex(Model model, @PathVariable("userId")int userId){

        model.addAttribute("vos",getQuestions(userId,0,10));

        User user=userService.getUser(userId);

        ViewObject vo=new ViewObject();
        vo.set("user",user);
        vo.set("commentCount",commentService.getUserCommentCount(userId));
        vo.set("followerCount",followService.getFollowerCount(EntityType.ENTITY_USER,userId));
        vo.set("followeeCount",followService.getFolloweeCount(userId,EntityType.ENTITY_USER));
        if(hostHolder.getUser()!=null){
            vo.set("followed",followService.isFollower(hostHolder.getUser().getId(),EntityType.ENTITY_USER,userId));
        }else {
            vo.set("followed",false);
        }
        model.addAttribute("profileUser",vo);

        return "profile";



    }


}
