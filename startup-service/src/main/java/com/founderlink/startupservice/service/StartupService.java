package com.founderlink.startupservice.service;

import com.founderlink.startupservice.entity.Startup;
import java.util.List;
import java.util.UUID;

public interface StartupService {

    Startup create(UUID userId, Startup startup);

    List<Startup> getMyStartups(UUID userId);

    Startup getById(UUID startupId);

    Startup update(UUID userId, UUID startupId, Startup updates);

    void delete(UUID userId, UUID startupId);

    List<Startup> findByIndustry(String industry);
}
