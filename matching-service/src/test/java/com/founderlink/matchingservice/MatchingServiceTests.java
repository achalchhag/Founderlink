package com.founderlink.matchingservice;

import com.founderlink.matchingservice.entity.MatchRecord;
import com.founderlink.matchingservice.repository.MatchRecordRepository;
import com.founderlink.matchingservice.service.MatchingServiceImpl;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MatchingServiceTests {

    @Mock
    private MatchRecordRepository matchRecordRepository;

    @InjectMocks
    private MatchingServiceImpl matchingService;

    @Test
    void createMatch_success() {
        UUID userId = UUID.randomUUID();
        UUID targetId = UUID.randomUUID();
        MatchRecord match = new MatchRecord(
                userId,
                targetId,
                "FOUNDER",
                "Alex Rivera",
                "CTO",
                "Experienced backend lead",
                "AI / SaaS",
                94,
                "High technical synergy"
        );

        when(matchRecordRepository.save(any(MatchRecord.class))).thenAnswer(inv -> inv.getArgument(0));

        MatchRecord saved = matchingService.createMatch(userId, match);
        assertNotNull(saved);
        assertEquals(userId, saved.getUserId());
        assertEquals("Alex Rivera", saved.getTargetName());
        assertEquals(94, saved.getMatchScore());
    }

    @Test
    void getMyMatches_success() {
        UUID userId = UUID.randomUUID();
        MatchRecord match = new MatchRecord(
                userId,
                UUID.randomUUID(),
                "FOUNDER",
                "Sarah Chen",
                "Product Lead",
                "Growth strategist",
                "FinTech",
                88,
                "Product strategy synergy"
        );

        when(matchRecordRepository.findByUserIdOrderByMatchScoreDesc(userId))
                .thenReturn(List.of(match));

        List<MatchRecord> results = matchingService.getMyMatches(userId);
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("Sarah Chen", results.get(0).getTargetName());
    }

    @Test
    void updateStatus_success() {
        UUID userId = UUID.randomUUID();
        UUID matchId = UUID.randomUUID();
        MatchRecord match = new MatchRecord(
                userId,
                UUID.randomUUID(),
                "FOUNDER",
                "Alex",
                "CTO",
                "Bio",
                "Tech",
                90,
                "Good"
        );
        match.setId(matchId);

        when(matchRecordRepository.findById(matchId)).thenReturn(Optional.of(match));
        when(matchRecordRepository.save(any(MatchRecord.class))).thenReturn(match);

        MatchRecord updated = matchingService.updateStatus(userId, matchId, "ACCEPTED");
        assertNotNull(updated);
        assertEquals("ACCEPTED", updated.getStatus());
    }
}
