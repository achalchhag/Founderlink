package com.founderlink.startupservice.service;

import com.founderlink.startupservice.entity.Startup;
import com.founderlink.startupservice.exception.StartupNotFoundException;
import com.founderlink.startupservice.exception.UnauthorizedAccessException;
import com.founderlink.startupservice.repository.StartupRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StartupServiceImpl implements StartupService {

    private final StartupRepository startups;

    public StartupServiceImpl(StartupRepository startups) {
        this.startups = startups;
    }

    @Override
    @Transactional
    public Startup create(UUID userId, Startup input) {
        Startup startup = new Startup(userId, input.getName());
        startup.update(input.getName(), input.getDescription(), input.getIndustry(),
                input.getStage(), input.getLookingFor(), input.getWebsiteUrl());
        return startups.save(startup);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Startup> getMyStartups(UUID userId) {
        return startups.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Startup getById(UUID startupId) {
        return startups.findById(startupId)
                .orElseThrow(() -> new StartupNotFoundException(startupId));
    }

    @Override
    @Transactional
    public Startup update(UUID userId, UUID startupId, Startup updates) {
        Startup startup = getById(startupId);
        if (!startup.getUserId().equals(userId)) {
            throw new UnauthorizedAccessException();
        }
        startup.update(updates.getName(), updates.getDescription(), updates.getIndustry(),
                updates.getStage(), updates.getLookingFor(), updates.getWebsiteUrl());
        return startup;
    }

    @Override
    @Transactional
    public void delete(UUID userId, UUID startupId) {
        Startup startup = getById(startupId);
        if (!startup.getUserId().equals(userId)) {
            throw new UnauthorizedAccessException();
        }
        startups.delete(startup);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Startup> findByIndustry(String industry) {
        return startups.findByIndustryIgnoreCaseOrderByCreatedAtDesc(industry);
    }
}
