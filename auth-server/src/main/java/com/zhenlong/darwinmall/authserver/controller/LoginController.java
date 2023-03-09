package com.zhenlong.darwinmall.authserver.controller;

import com.alibaba.fastjson.TypeReference;
import com.zhenlong.common.constant.AuthServerConstant;
import com.zhenlong.common.utils.R;
import com.zhenlong.common.vo.MemberRespVo;
import com.zhenlong.darwinmall.authserver.feign.MemberFeignService;
import com.zhenlong.darwinmall.authserver.vo.UserLoginVo;
import com.zhenlong.darwinmall.authserver.vo.UserRegisterVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class LoginController {

    @Autowired
    MemberFeignService memberFeignService;

    /**
     * register api
     *
     * @param vo
     * @param result
     * @param redirectAttributes
     * @return
     */
    @PostMapping("/register")
    public String register(@Valid UserRegisterVo vo, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            Map<String, String> errors = result.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
            redirectAttributes.addFlashAttribute("errors", errors);
            //validation error, return to register page
            return "redirect:http://auth.darwinmall.com/reg.html";
        }

        R r = memberFeignService.register(vo);
        if(r.getCode()==0){
            //register success, return to login page
            return "redirect:http://auth.darwinmall.com/login.html";
        } else {
            Map<String, String> errors = new HashMap<>();
            errors.put("msg", r.getData("msg", new TypeReference<String>() {
            }));
            redirectAttributes.addFlashAttribute("errors", errors);
            return "redirect:http://auth.darwinmall.com/reg.html";
        }
        //TODO 验证码服务，暂时不做
    }

    @PostMapping("/login")
    public String login(UserLoginVo vo, RedirectAttributes redirectAttributes, HttpSession session) {
        R r = memberFeignService.login(vo);
        if (r.getCode() == 0) {
            MemberRespVo data = r.getData("data", new TypeReference<MemberRespVo>() {
            });
            //login success and put the info in the redis
            session.setAttribute(AuthServerConstant.LOGIN_USER, data);
            return "redirect:http://darwinmall.com";
        } else {
            Map<String, String> errors = new HashMap<>();
            errors.put("msg", r.getData("msg", new TypeReference<String>() {
            }));
            redirectAttributes.addFlashAttribute("errors", errors);
            return "redirect:http://auth.darwinmall.com/login.html";
        }

    }

    /**
     * login api
     *
     * @param session
     * @return
     */
    @GetMapping("/login.html")
    public String loginPage(HttpSession session) {
        Object attribute = session.getAttribute(AuthServerConstant.LOGIN_USER);
        if (attribute == null) {
            return "login";
        } else {
            return "redirect:http://darwinmall.com";
        }

    }
}
