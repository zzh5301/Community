package com.example.wenda.controller;

import com.example.wenda.async.EventModel;
import com.example.wenda.async.EventProducer;
import com.example.wenda.async.EventType;
import com.example.wenda.service.UserService;
import com.example.wenda.util.VerifyCodeUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

/**
 * Created by lenovo on 2017/5/25.
 */

@Controller
public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    private static final int w=100;
    private static final int h=40;


    @Autowired
    UserService userService;

    @Autowired
    EventProducer eventProducer;

    @RequestMapping(path = {"/reg/"},method = {RequestMethod.POST})
    public String reg(Model model, @RequestParam("username") String username, @RequestParam("password") String password,
                      @RequestParam("next") String next, @RequestParam(value = "rememberme",defaultValue = "false") boolean rememberme,
                      @RequestParam("verifycode") String verifycode,
                      HttpServletResponse response, HttpSession session){
        try{
            boolean flag=verifycode.toLowerCase().equals(session.getAttribute("code"));

            Map<String,Object> map=userService.register(username,password,false);
            if(map.containsKey("ticket")){
                Cookie cookie=new Cookie("ticket",map.get("ticket").toString());
                cookie.setPath("/");

                if(rememberme){
                    cookie.setMaxAge(3600*24*5);
                }
                response.addCookie(cookie);
                if(StringUtils.isNotBlank(next))
                    return "redirect:"+next;
                return "redirect:/";

            }else{
                model.addAttribute("msg",map.get("msg"));
                return "login";

            }
        }catch (Exception e){
            logger.error("注册异常"+e.getMessage());
            model.addAttribute("msg","服务器错误");
            return "login";
        }
    }

    @RequestMapping(path = {"/reglogin"},method = {RequestMethod.GET})
    public String regloginPage(Model modle,@RequestParam(value="next",required = false) String next){

        modle.addAttribute("next",next);
        return "login";

    }

    public String login(Model model, @RequestParam("username") String username,
                        @RequestParam("password") String password,
                        @RequestParam(value="next", required = false) String next,
                        @RequestParam(value="rememberme", defaultValue = "false") boolean rememberme,
                        @RequestParam("verifycode") String verifycode,
                        HttpServletResponse response,HttpSession session) {
        try {
            String correctCode=(String) session.getAttribute("code");

            boolean flag=verifycode.toLowerCase().equals(correctCode);

            Map<String, Object> map = userService.login(username, password,flag);

            if (map.containsKey("ticket")) {
                Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
                cookie.setPath("/");
                if (rememberme) {
                    cookie.setMaxAge(3600*24*5);
                }
                response.addCookie(cookie);

                eventProducer.fireEvent(new EventModel(EventType.LOGIN)
                        .setExt("username", username).setExt("email", "zjuyxy@qq.com")
                        .setActorId((int)map.get("userId")));

                if (StringUtils.isNotBlank(next)) {
                    return "redirect:" + next;
                }
                return "redirect:/";
            } else {
                model.addAttribute("msg", map.get("msg"));
                return "login";
            }

        } catch (Exception e) {
            logger.error("登陆异常" + e.getMessage());
            return "login";
        }
    }

    @RequestMapping(path={"/logout"},method = {RequestMethod.GET,RequestMethod.POST})
    public String logout(@CookieValue("ticket") String ticket){
        userService.logout(ticket);
        return "redirect:/";
    }

    @RequestMapping(path={"/verifyCode"})
    public void verifyCode(HttpServletResponse response,HttpSession session)throws IOException{
        String verifyCode= VerifyCodeUtils.generateVerifyCode(4);

        response.setContentType("imge/jpeg");
        session.setAttribute("code",verifyCode.toLowerCase());

        VerifyCodeUtils.outputImage(w,h,response.getOutputStream(),verifyCode);
    }


}
