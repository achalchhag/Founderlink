package com.founderlink.profileservice.service;

import com.founderlink.profileservice.entity.Profile;
import java.util.List;
import java.util.UUID;

public interface ProfileService {

    Profile create(UUID userId, Profile profile);

    Profile getMine(UUID userId);

    Profile update(UUID userId, Profile updates);

    void delete(UUID userId);

    Profile getByUserId(UUID userId);

    List<Profile> search(String location, String skill, String headline);
}
