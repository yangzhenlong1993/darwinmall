package com.zhenlong.darwinmall.authserver.feign;

import com.zhenlong.common.utils.R;
import com.zhenlong.darwinmall.authserver.vo.SocialUser;
import com.zhenlong.darwinmall.authserver.vo.UserLoginVo;
import com.zhenlong.darwinmall.authserver.vo.UserRegisterVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("member")
public interface MemberFeignService {
    @PostMapping("/member/member/register")
    public R register(@RequestBody UserRegisterVo vo);

    @PostMapping("/member/member/login")
    public R login(@RequestBody UserLoginVo vo);

    @PostMapping("/member/member/oauth2/login")
    public R oAuthLogin(@RequestBody SocialUser SocialUser) throws Exception;
}
