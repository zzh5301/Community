package com.example.wenda.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * Created by lenovo on 2017/5/24.
 */

@Controller
public class IndexController {


    @RequestMapping(path={"/","/profile/{userId}"})
    @ResponseBody
    public  String profile(@PathVariable("userId") int userId,
                           @RequestParam(value = "type",required = false) int type){

        return String.format("Profile page %s /%d",userId);
    }
}
