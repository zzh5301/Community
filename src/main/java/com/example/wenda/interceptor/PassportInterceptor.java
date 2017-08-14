package com.example.wenda.interceptor;

import com.example.wenda.dao.LoginTicketDAO;
import com.example.wenda.dao.UserDAO;
import com.example.wenda.model.HostHolder;
import com.example.wenda.model.LoginTicket;
import com.example.wenda.model.User;
import com.example.wenda.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * Created by lenovo on 2017/5/25.
 */
public class PassportInterceptor implements HandlerInterceptor{

    @Autowired
    private LoginTicketDAO loginTicketDAO;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;


    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {

        String ticket=null;
        if(httpServletRequest.getCookies()!=null){
            for(Cookie cookie:httpServletRequest.getCookies()){
                if(cookie.getName().equals("ticket")){
                    ticket=cookie.getValue();
                    break;
                }
            }
        }


        if(ticket!=null){

            LoginTicket loginTicket=loginTicketDAO.selectByTicket(ticket);
            if(loginTicket==null||loginTicket.getExpired().before(new Date())||loginTicket.getStatus()!=0){
                return true;
            }

            User user=userService.getUser(loginTicket.getUserId());
            hostHolder.setUser(user);
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
        if(modelAndView!=null&&hostHolder.getUser()!=null){
            modelAndView.addObject("user",hostHolder.getUser());
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        hostHolder.clear();
    }
}
