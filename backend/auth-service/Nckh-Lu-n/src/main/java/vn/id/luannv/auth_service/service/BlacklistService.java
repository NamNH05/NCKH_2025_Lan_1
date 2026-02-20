package vn.id.luannv.auth_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.id.luannv.auth_service.entity.BlacklistedToken;
import vn.id.luannv.auth_service.repository.BlacklistedTokenRepository;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class BlacklistService {

    private final BlacklistedTokenRepository blacklistRepository;

    public void blacklistToken(String token, Instant expiredAt) {
        if (!blacklistRepository.existsByToken(token)) {
            blacklistRepository.save(
                    BlacklistedToken.builder()
                            .token(token)
                            .expiredAt(expiredAt)
                            .build()
            );
        }
    }

    public boolean isBlacklisted(String token) {
        return blacklistRepository.existsByToken(token);
    }
}
