package com.mip.sharebnb.exception;

public class PrePasswordNotMatchedException extends RuntimeException{
    public PrePasswordNotMatchedException(String message) {
        super(message);
    }
}
