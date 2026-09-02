package com.founderlink.discoveryservice.client;

import com.founderlink.discoveryservice.dto.response.ProfileResponse;
import java.util.List;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "PROFILE-SERVICE")
public interface ProfileClient {

    @GetMapping("/api/profiles/{userId}")
    ProfileResponse getByUserId(@PathVariable("userId") UUID userId);

    @GetMapping("/api/profiles")
    List<ProfileResponse> search(@RequestParam(value = "location", required = false) String location,
                                 @RequestParam(value = "skill", required = false) String skill,
                                 @RequestParam(value = "headline", required = false) String headline);
}
