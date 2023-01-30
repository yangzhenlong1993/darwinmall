package com.zhenlong.darwinmall.authserver.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Data
public class UserRegisterVo {
    @NotEmpty(message = "username cannot be empty")
    @Length(min=6,max = 18,message = "username length must be 6~18")
    private String username;
    @NotEmpty(message = "password cannot be empty")
    @Length(min=6,max = 18,message = "username length must be 6~18")
    private String password;
    @Pattern(regexp = "^[04][0-9]{8}$",message = "phone number format not correct")
    private String phone;
    private String code;
}
