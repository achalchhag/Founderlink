package com.founderlink.startupservice.exception;

public class UnauthorizedAccessException extends RuntimeException {

    public UnauthorizedAccessException() {
        super("You are not allowed to modify this startup");
    }
}
