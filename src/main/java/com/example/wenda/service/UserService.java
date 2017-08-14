package com.example.wenda.service;

import com.example.wenda.dao.LoginTicketDAO;
import com.example.wenda.dao.QuestionDAO;
import com.example.wenda.dao.UserDAO;
import com.example.wenda.model.LoginTicket;
import com.example.wenda.model.User;
import com.example.wenda.util.WendaUtil;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.jws.soap.SOAPBinding;
import java.util.*;

/**
 * Created by lenovo on 2017/5/25.
 */

@Service
public class UserService {

    @Autowired
    UserDAO userDAO;

    @Autowired
    LoginTicketDAO loginTicketDAO;

    public User selectByName(String name){return userDAO.selectByName(name);}


    public Map<String,Object> register(String username,String password,boolean flag){

        Map<String,Object> map=new HashMap<>();

        if(!flag){
            map.put("msg","验证码错误");
            return map;
        }

        if(StringUtils.isBlank(username)){
            map.put("msg","用户名不能为空");
            return map;
        }

        if(StringUtils.isBlank(password)){
            map.put("msg","密码不能为空");
            return map;
        }

        User user=userDAO.selectByName(username);
        if(user!=null){
            map.put("msg","用户名已经备注册");
            return  map;
        }

        user=new User();
        user.setName(username);
        user.setSalt(UUID.randomUUID().toString().substring(0,5));
        String head=String.format("http://images.nowcoder.com/head/%dt.png",new Random().nextInt(1000));
        user.setHeadUrl(head);
        user.setPassword(WendaUtil.MD5(password+user.getSalt()));
        userDAO.addUser(user);

        String ticket=addLoginTicket(user.getId());

        map.put("ticket",ticket);
        return map;
    }

    public Map<String,Object> login(String username,String password,boolean flag){

        Map<String,Object> map=new HashMap<>();
        if(!flag){
            map.put("msg","验证码错误");
            return map;
        }

        if (StringUtils.isBlank(username)) {
            map.put("msg", "用户名不能为空");
            return map;
        }

        if (StringUtils.isBlank(password)) {
            map.put("msg", "密码不能为空");
            return map;
        }

        User user = userDAO.selectByName(username);

        if (user == null) {
            map.put("msg", "用户名不存在");
            return map;
        }
        if(!WendaUtil.MD5(password+user.getSalt()).equals(user.getPassword())){
            map.put("msg","密码不正确");
            return map;
        }
        String ticket=addLoginTicket(user.getId());
        map.put("ticket",ticket);
        map.put("userId",user.getId());
        return map;
    }

    public User getUser(int id){
        return userDAO.selectById(id);
    }

    public void logout(String ticket){loginTicketDAO.updateStatus(ticket,1);}

    private String addLoginTicket(int userId){

        LoginTicket ticket=new LoginTicket();
        ticket.setUserId(userId);
        Date date=new Date();
        date.setTime(date.getTime()+100*3600*24);
        ticket.setExpired(date);
        ticket.setStatus(0);
        ticket.setTicket(UUID.randomUUID().toString().replaceAll("-",""));
        loginTicketDAO.addTicket(ticket);
        return ticket.getTicket();
    }



}
