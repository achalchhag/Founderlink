package com.founderlink.startupservice.exception;

import java.util.UUID;

public class StartupNotFoundException extends RuntimeException {

    public StartupNotFoundException(UUID startupId) {
        super("Startup not found: " + startupId);
    }
}
