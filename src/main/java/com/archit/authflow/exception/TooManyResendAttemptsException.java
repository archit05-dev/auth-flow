package com.archit.authflow.exception;

public class TooManyResendAttemptsException extends RuntimeException {

    public TooManyResendAttemptsException(String message) {
        super(message);
    }
}