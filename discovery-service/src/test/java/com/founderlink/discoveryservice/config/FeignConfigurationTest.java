package com.founderlink.discoveryservice.config;

import static org.assertj.core.api.Assertions.assertThat;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

class FeignConfigurationTest {

    @AfterEach
    void clearRequestContext() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void forwardsIncomingAuthorizationHeader() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer test-token");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        RequestTemplate template = new RequestTemplate();
        RequestInterceptor interceptor = new FeignConfiguration().bearerTokenForwardingInterceptor();

        interceptor.apply(template);

        assertThat(template.headers()).containsEntry("Authorization", java.util.List.of("Bearer test-token"));
    }
}
