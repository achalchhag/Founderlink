package com.founderlink.startupservice.repository;

import com.founderlink.startupservice.entity.Startup;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StartupRepository extends JpaRepository<Startup, UUID> {

    List<Startup> findByUserIdOrderByCreatedAtDesc(UUID userId);

    List<Startup> findByIndustryIgnoreCaseOrderByCreatedAtDesc(String industry);
}
