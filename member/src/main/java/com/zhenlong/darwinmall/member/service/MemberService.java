package com.zhenlong.darwinmall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhenlong.common.utils.PageUtils;
import com.zhenlong.darwinmall.member.entity.MemberEntity;
import com.zhenlong.darwinmall.member.exception.PhoneExistException;
import com.zhenlong.darwinmall.member.exception.UsernameExistException;
import com.zhenlong.darwinmall.member.vo.MemberLoginVo;
import com.zhenlong.darwinmall.member.vo.MemberRegisterVo;
import com.zhenlong.darwinmall.member.vo.SocialUser;

import java.util.Map;

/**
 * 会员
 *
 * @author zhenlong
 * @email yangzhenlong1993@gmail.com
 * @date 2022-12-27 20:38:12
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void register(MemberRegisterVo vo);

    void checkPhoneUnique(String phone) throws PhoneExistException;

    void checkUsernameUnique(String username) throws UsernameExistException;

    MemberEntity login(MemberLoginVo vo);

    MemberEntity login(SocialUser socialUser) throws Exception;
}

