package com.founderlink.profileservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.founderlink.profileservice.dto.request.CreateProfileRequest;
import com.founderlink.profileservice.dto.request.UpdateProfileRequest;
import com.founderlink.profileservice.dto.response.ProfileResponse;
import com.founderlink.profileservice.entity.Profile;
import com.founderlink.profileservice.exception.DuplicateProfileException;
import com.founderlink.profileservice.exception.ProfileNotFoundException;
import com.founderlink.profileservice.mapper.ProfileMapper;
import com.founderlink.profileservice.repository.ProfileRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProfileServiceImplTest {

    @Mock
    private ProfileRepository profiles;

    private ProfileService service;
    private final UUID userId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        service = new ProfileServiceImpl(profiles, new ProfileMapper());
    }

    @Test
    void createsProfileForAuthenticatedUser() {
        when(profiles.save(any(Profile.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProfileResponse response = service.create(userId, createRequest());

        assertThat(response.userId()).isEqualTo(userId);
        verify(profiles).existsByUserId(userId);
        verify(profiles).save(any(Profile.class));
    }

    @Test
    void rejectsDuplicateProfileCreation() {
        when(profiles.existsByUserId(userId)).thenReturn(true);

        assertThatThrownBy(() -> service.create(userId, createRequest())).isInstanceOf(DuplicateProfileException.class);
        verify(profiles, never()).save(any());
    }

    @Test
    void getsAndUpdatesOwnProfile() {
        Profile profile = new Profile(userId);
        when(profiles.findByUserId(userId)).thenReturn(Optional.of(profile));

        ProfileResponse response = service.update(userId, new UpdateProfileRequest("Updated", null, null, null, 4, null, null, null));

        assertThat(response.userId()).isEqualTo(userId);
        assertThat(profile.getHeadline()).isEqualTo("Updated");
        assertThat(profile.getExperienceYears()).isEqualTo(4);
    }

    @Test
    void getsOwnProfile() {
        Profile profile = new Profile(userId);
        when(profiles.findByUserId(userId)).thenReturn(Optional.of(profile));

        ProfileResponse response = service.getMine(userId);

        assertThat(response.userId()).isEqualTo(userId);
    }

    @Test
    void deletesOwnProfile() {
        Profile profile = new Profile(userId);
        when(profiles.findByUserId(userId)).thenReturn(Optional.of(profile));

        service.delete(userId);

        verify(profiles).delete(profile);
    }

    @Test
    void returnsNotFoundForMissingProfile() {
        when(profiles.findByUserId(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getMine(userId)).isInstanceOf(ProfileNotFoundException.class);
    }

    private CreateProfileRequest createRequest() {
        return new CreateProfileRequest("Founder", "Bio", "Mumbai", "Java", 3,
                "https://example.com", "https://linkedin.com/in/example", "https://github.com/example");
    }
}
