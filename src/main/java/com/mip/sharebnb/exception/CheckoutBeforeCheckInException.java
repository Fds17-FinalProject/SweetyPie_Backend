package com.mip.sharebnb.exception;

public class CheckoutBeforeCheckInException extends RuntimeException{
    public CheckoutBeforeCheckInException() {
        super("Checkout is before check-in");
    }
}
