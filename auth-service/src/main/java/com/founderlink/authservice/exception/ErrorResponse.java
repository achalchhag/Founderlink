package com.founderlink.authservice.exception;
import java.time.Instant; import java.util.Map; public record ErrorResponse(Instant timestamp,int status,String message,String path,Map<String,String> fieldErrors){}
