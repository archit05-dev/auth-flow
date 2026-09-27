package com.archit.authflow.exception;

public class AlreadyVerifiedException extends RuntimeException {

    public AlreadyVerifiedException(String message) {
        super(message);
    }
}