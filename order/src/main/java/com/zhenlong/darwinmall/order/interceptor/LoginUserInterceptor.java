package com.zhenlong.darwinmall.order.interceptor;

import com.zhenlong.common.constant.AuthServerConstant;
import com.zhenlong.common.vo.MemberRespVo;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class LoginUserInterceptor implements HandlerInterceptor {
    public static ThreadLocal<MemberRespVo> loginUser = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        String uri = request.getRequestURI();
//        AntPathMatcher antPathMatcher = new AntPathMatcher();
//        boolean match = antPathMatcher.match("/order/order/status/**", uri);
//        boolean match1 = antPathMatcher.match("/payed/notify", uri);
//        if (match || match1) {
//            return true;
//        }

        if (loginUser.get() != null) {
            return true;
        } else {
            MemberRespVo attribute = (MemberRespVo) request.getSession().getAttribute(AuthServerConstant.LOGIN_USER);
            if (attribute != null) {
                loginUser.set(attribute);
                return true;
            }
        }
        request.getSession().setAttribute("msg", "请先进行登录");
        response.sendRedirect("http://my.web.com/login.html");
        return false;
    }
}