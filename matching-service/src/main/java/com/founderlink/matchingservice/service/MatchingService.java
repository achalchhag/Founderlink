package com.founderlink.matchingservice.service;

import com.founderlink.matchingservice.entity.MatchRecord;
import java.util.List;
import java.util.UUID;

public interface MatchingService {

    MatchRecord createMatch(UUID userId, MatchRecord matchRecord);

    List<MatchRecord> getMyMatches(UUID userId);

    List<MatchRecord> getMatchesByType(UUID userId, String matchType);

    MatchRecord getById(UUID matchId);

    MatchRecord updateStatus(UUID userId, UUID matchId, String status);

    void deleteMatch(UUID userId, UUID matchId);

    List<MatchRecord> generateRecommendations(UUID userId, String role, String skills);
}
