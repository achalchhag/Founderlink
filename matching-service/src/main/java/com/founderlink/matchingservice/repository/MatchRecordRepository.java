package com.founderlink.matchingservice.repository;

import com.founderlink.matchingservice.entity.MatchRecord;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchRecordRepository extends JpaRepository<MatchRecord, UUID> {

    List<MatchRecord> findByUserIdOrderByMatchScoreDesc(UUID userId);

    List<MatchRecord> findByUserIdAndMatchTypeOrderByMatchScoreDesc(UUID userId, String matchType);

    Optional<MatchRecord> findByUserIdAndTargetId(UUID userId, UUID targetId);

    void deleteByUserIdAndId(UUID userId, UUID id);
}
