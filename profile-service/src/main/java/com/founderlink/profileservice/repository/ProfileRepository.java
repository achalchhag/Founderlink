package com.founderlink.profileservice.repository;

import com.founderlink.profileservice.entity.Profile;
import java.util.Optional;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<Profile, UUID> {
    Optional<Profile> findByUserId(UUID userId);
    boolean existsByUserId(UUID userId);

    List<Profile> findAllByOrderByCreatedAtDesc();

    @Query("""
            select p from Profile p
            where (cast(:location as string) is null or lower(p.location) like concat('%', lower(cast(:location as string)), '%'))
              and (cast(:skill as string) is null or lower(p.skills) like concat('%', lower(cast(:skill as string)), '%'))
              and (cast(:headline as string) is null or lower(p.headline) like concat('%', lower(cast(:headline as string)), '%'))
            order by p.createdAt desc
            """)
    List<Profile> search(@Param("location") String location, @Param("skill") String skill,
                         @Param("headline") String headline);
}
