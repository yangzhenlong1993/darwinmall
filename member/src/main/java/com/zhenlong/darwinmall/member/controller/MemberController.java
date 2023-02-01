package com.zhenlong.darwinmall.member.controller;

import com.zhenlong.common.exception.BizCodeEnum;
import com.zhenlong.common.utils.PageUtils;
import com.zhenlong.common.utils.R;
import com.zhenlong.darwinmall.member.entity.MemberEntity;
import com.zhenlong.darwinmall.member.exception.PhoneExistException;
import com.zhenlong.darwinmall.member.exception.UsernameExistException;
import com.zhenlong.darwinmall.member.feign.CouponFeignService;
import com.zhenlong.darwinmall.member.service.MemberService;
import com.zhenlong.darwinmall.member.vo.MemberLoginVo;
import com.zhenlong.darwinmall.member.vo.MemberRegisterVo;
import com.zhenlong.darwinmall.member.vo.SocialUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;


/**
 * 会员
 *
 * @author zhenlong
 * @email yangzhenlong1993@gmail.com
 * @date 2022-12-27 20:38:12
 */
@RestController
@RequestMapping("member/member")
public class MemberController {
    @Autowired
    private MemberService memberService;
    @Autowired
    private CouponFeignService couponFeignService;

    @PostMapping("/oauth2/login")
    public R oAuthLogin(@RequestBody SocialUser SocialUser) throws Exception {
        MemberEntity entity = memberService.login(SocialUser);
        if (entity != null) {
            return R.ok().setData(entity);
        } else {
            return R.error(BizCodeEnum.LOGIN_FAILED_EXCEPTION.getCode(), BizCodeEnum.LOGIN_FAILED_EXCEPTION.getMsg());
        }
    }

    @RequestMapping("/coupons")
    public R test() {
        MemberEntity member = new MemberEntity();
        member.setNickname("张三");
        R memberCoupons = couponFeignService.memberCoupons();

        return R.ok().put("member", member).put("coupon", memberCoupons.get("coupons"));
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("member:member:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = memberService.queryPage(params);

        return R.ok().put("page", page);
    }

    @PostMapping("/register")
    public R register(@RequestBody MemberRegisterVo vo) {
        try {
            memberService.register(vo);
        } catch (PhoneExistException e) {
            return R.error(BizCodeEnum.PHONE_EXIST_EXCEPTION.getCode(), BizCodeEnum.PHONE_EXIST_EXCEPTION.getMsg());
        } catch (UsernameExistException e) {
            return R.error(BizCodeEnum.USER_EXIST_EXCEPTION.getCode(), BizCodeEnum.USER_EXIST_EXCEPTION.getMsg());
        }

        return R.ok();
    }

    @PostMapping("/login")
    public R login(@RequestBody MemberLoginVo vo){
       MemberEntity entity= memberService.login(vo);
       if(entity!=null){
           return R.ok();
       }else {
           return R.error(BizCodeEnum.LOGIN_FAILED_EXCEPTION.getCode(),BizCodeEnum.LOGIN_FAILED_EXCEPTION.getMsg());
       }
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("member:member:info")
    public R info(@PathVariable("id") Long id) {
        MemberEntity member = memberService.getById(id);

        return R.ok().put("member", member);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("member:member:save")
    public R save(@RequestBody MemberEntity member) {
        memberService.save(member);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("member:member:update")
    public R update(@RequestBody MemberEntity member) {
        memberService.updateById(member);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("member:member:delete")
    public R delete(@RequestBody Long[] ids) {
        memberService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
