package com.zhenlong.darwinmall.authserver.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.zhenlong.common.utils.HttpUtils;
import com.zhenlong.common.utils.R;
import com.zhenlong.common.vo.MemberRespVo;
import com.zhenlong.darwinmall.authserver.feign.MemberFeignService;
import com.zhenlong.darwinmall.authserver.vo.SocialUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * handle social platform login request
 */
@Slf4j
@Controller
public class OAuth2Controller {
    @Autowired
    MemberFeignService memberFeignService;

    @GetMapping("/oauth2.0/weibo/success")
    public String weibo(@RequestParam("code") String code, HttpSession session) throws Exception {
        Map<String, String> map = new HashMap<>();
        //TODO 社交登录授权信息
        map.put("client_id", "2347423033");
        map.put("client_secret", "affa9f40b2273a50dc65338ff9f779ab");
        map.put("grant_type", "authorization_code");
        map.put("redirect_uri", "http://auth.darwinmall.com/oauth2.0/weibo/success");
        map.put("code", code);
        //1.根据code换取accessToken
        HttpResponse response = HttpUtils.doPost("https://api.weibo.com", "/oauth2/access_token", "post", new HashMap<>(), map, new HashMap<>());
        //2.处理返回信息
        if (response.getStatusLine().getStatusCode() == 200) {
            //获取到了accessToken
            String json = EntityUtils.toString(response.getEntity());
            SocialUser socialUser = JSON.parseObject(json, SocialUser.class);
            //知道当前是哪个社交用户
            //当前用户如果是第一次进网站，自动注册进来，为当前社交用户生成一个会员信息账号，以后这个社交账号就对应指定的会员
            //登录或者注册这个社交用户
            R oAuthLogin = memberFeignService.oAuthLogin(socialUser);
            if (oAuthLogin.getCode() == 0) {
                MemberRespVo data = oAuthLogin.getData("data", new TypeReference<MemberRespVo>() {
                });
                log.info("登录成功，用户信息：{}", data.toString());
                // 第一次使用session，命令浏览器保存卡号，JessionId这个cookie
                //以后浏览器访问哪个网站就会带上这个网站的cookie
                //子域之间，darwinmall.com auth.darwinmall.com......
                //发卡的时候（指定作用域名为父域名），即使是子域系统发的卡，也能让父域直接使用
                session.setAttribute("loginUser", data);
                //2. 登录成功就跳回首页
                return "redirect:http://darwinmall.com";
            } else {
                return "redirect:http://auth.darwinmall.com/login.html";
            }
        } else {
            return "redirect:http://auth.darwinmall.com/login.html";
        }

    }
}
