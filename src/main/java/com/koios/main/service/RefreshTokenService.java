package com.koios.main.service;

import com.koios.main.model.RefreshToken;
import com.koios.main.model.User;
import com.koios.main.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenService {
    @Value("${jwt.refresh.expiry}")
    private long JWT_REFRESH_EXP;

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public RefreshToken findByRefreshToken(String refreshToken) {
        return refreshTokenRepository.findByRefreshToken(refreshToken);
    }

    public boolean isRefreshTokenValid(RefreshToken refreshToken) {
        return refreshToken.getExpDate().compareTo(Instant.now()) > 0;
    }

    public RefreshToken createRefreshToken(User user) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setExpDate(Instant.now().plusMillis(JWT_REFRESH_EXP));
        refreshToken.setRefreshToken(UUID.randomUUID().toString());
        refreshTokenRepository.save(refreshToken);
        return refreshToken;
    }
}
