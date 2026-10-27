package com.adent.identity.repository;

import com.adent.identity.domain.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, UUID> {
    Optional<PasswordResetToken> findTopByUserIdAndChannelAndUsedAtIsNullOrderByCreatedAtDesc(UUID userId, String channel);
}
