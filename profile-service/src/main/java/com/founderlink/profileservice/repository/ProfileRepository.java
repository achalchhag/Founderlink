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
    void deleteByUserId(UUID userId);

    @Query("""
            select p from Profile p
            where (:location is null or lower(p.location) like lower(concat('%', :location, '%')))
              and (:skill is null or lower(p.skills) like lower(concat('%', :skill, '%')))
              and (:headline is null or lower(p.headline) like lower(concat('%', :headline, '%')))
            order by p.createdAt desc
            """)
    List<Profile> search(@Param("location") String location, @Param("skill") String skill,
                         @Param("headline") String headline);
}
