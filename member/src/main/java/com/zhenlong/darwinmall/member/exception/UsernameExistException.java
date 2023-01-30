package com.zhenlong.darwinmall.member.exception;

public class UsernameExistException extends RuntimeException{
    public UsernameExistException(){
        super("username already exist");
    }
}
