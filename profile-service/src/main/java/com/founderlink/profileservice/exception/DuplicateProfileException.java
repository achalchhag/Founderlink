package com.founderlink.profileservice.exception;

public class DuplicateProfileException extends RuntimeException {
    public DuplicateProfileException() {
        super("A profile already exists for this user");
    }
}
