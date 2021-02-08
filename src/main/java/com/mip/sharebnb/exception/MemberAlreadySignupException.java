package com.mip.sharebnb.exception;

public class MemberAlreadySignupException extends RuntimeException{
    public MemberAlreadySignupException(String message) {
        super(message);
    }
}
