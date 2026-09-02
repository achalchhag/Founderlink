package com.founderlink.discoveryservice.exception;

public class DownstreamProfileServiceException extends RuntimeException {
    public DownstreamProfileServiceException(Throwable cause) {
        super("Profile Service is currently unavailable", cause);
    }
}
