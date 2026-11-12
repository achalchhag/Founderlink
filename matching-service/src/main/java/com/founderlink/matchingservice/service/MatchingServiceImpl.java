package com.founderlink.matchingservice.service;

import com.founderlink.matchingservice.entity.MatchRecord;
import com.founderlink.matchingservice.repository.MatchRecordRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MatchingServiceImpl implements MatchingService {

    private final MatchRecordRepository matchRecordRepository;

    public MatchingServiceImpl(MatchRecordRepository matchRecordRepository) {
        this.matchRecordRepository = matchRecordRepository;
    }

    @Override
    @Transactional
    public MatchRecord createMatch(UUID userId, MatchRecord matchRecord) {
        matchRecord.setUserId(userId);
        return matchRecordRepository.save(matchRecord);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MatchRecord> getMyMatches(UUID userId) {
        return matchRecordRepository.findByUserIdOrderByMatchScoreDesc(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MatchRecord> getMatchesByType(UUID userId, String matchType) {
        return matchRecordRepository.findByUserIdAndMatchTypeOrderByMatchScoreDesc(userId, matchType);
    }

    @Override
    @Transactional(readOnly = true)
    public MatchRecord getById(UUID matchId) {
        return matchRecordRepository.findById(matchId)
                .orElseThrow(() -> new NoSuchElementException("Match record not found with id: " + matchId));
    }

    @Override
    @Transactional
    public MatchRecord updateStatus(UUID userId, UUID matchId, String status) {
        MatchRecord match = getById(matchId);
        if (match.getUserId().equals(userId)) {
            match.setStatus(status);
            return matchRecordRepository.save(match);
        }
        return match;
    }

    @Override
    @Transactional
    public void deleteMatch(UUID userId, UUID matchId) {
        MatchRecord match = getById(matchId);
        if (match.getUserId().equals(userId)) {
            matchRecordRepository.delete(match);
        }
    }

    @Override
    @Transactional
    public List<MatchRecord> generateRecommendations(UUID userId, String role, String skills) {
        List<MatchRecord> existing = matchRecordRepository.findByUserIdOrderByMatchScoreDesc(userId);
        if (!existing.isEmpty()) {
            return existing;
        }

        // Generate initial matches and persist in database using JPA
        List<MatchRecord> curated = new ArrayList<>();
        curated.add(new MatchRecord(
                userId,
                UUID.fromString("11111111-1111-1111-1111-111111111111"),
                "FOUNDER",
                "Alex Rivera",
                "Technical Co-Founder / Full Stack Engineer",
                "Passionate about building scalable AI-native SaaS products.",
                "AI / SaaS",
                94,
                "Strong technical match with complementary backend and full-stack capabilities."
        ));

        curated.add(new MatchRecord(
                userId,
                UUID.fromString("22222222-2222-2222-2222-222222222222"),
                "FOUNDER",
                "Sarah Chen",
                "Product & Growth Lead",
                "Ex-Fintech PM with a track record of scaling user acquisition 0 to 1M.",
                "FinTech / Growth",
                88,
                "Excellent commercial and product synergy to accelerate go-to-market."
        ));

        curated.add(new MatchRecord(
                userId,
                UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"),
                "STARTUP",
                "AgentFlow AI",
                "CTO / Backend Lead",
                "Autonomous workflow orchestration platform for enterprise customer support.",
                "Artificial Intelligence",
                92,
                "Seeking technical co-founder with backend and distributed systems experience."
        ));

        return matchRecordRepository.saveAll(curated);
    }
}
