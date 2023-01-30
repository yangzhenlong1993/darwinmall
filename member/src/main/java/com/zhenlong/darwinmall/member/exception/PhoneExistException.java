package com.zhenlong.darwinmall.member.exception;

public class PhoneExistException extends RuntimeException{
    public PhoneExistException(){
        super("phone number already exist");
    }
}
