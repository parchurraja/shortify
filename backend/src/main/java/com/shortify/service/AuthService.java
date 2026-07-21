package com.shortify.service;

import com.shortify.monitoring.MetricsService;
import com.shortify.dto.request.LoginRequest;
import com.shortify.dto.request.RegisterRequest;
import com.shortify.dto.request.TokenRefreshRequest;
import com.shortify.dto.response.AuthResponse;
import com.shortify.dto.response.TokenRefreshResponse;
import com.shortify.entity.RefreshToken;
import com.shortify.entity.Role;
import com.shortify.entity.User;
import com.shortify.exception.DuplicateResourceException;
import com.shortify.exception.UnauthorizedException;
import com.shortify.repository.RefreshTokenRepository;
import com.shortify.repository.UserRepository;
import com.shortify.security.JwtUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final MetricsService metricsService;

    @Value("${app.jwt.refresh-expiration-days:7}")
    private long refreshExpirationDays;

    public AuthService(UserRepository userRepository,
                       RefreshTokenRepository refreshTokenRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtils jwtUtils,
                       AuthenticationManager authenticationManager,
                       MetricsService metricsService) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.authenticationManager = authenticationManager;
        this.metricsService = metricsService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email is already registered");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.ROLE_USER)
                .build();

        User savedUser = userRepository.save(user);
        
        String accessToken = jwtUtils.generateToken(savedUser.getEmail());
        String refreshToken = createRefreshToken(savedUser);

        return buildAuthResponse(savedUser, accessToken, refreshToken);
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        long startTime = System.currentTimeMillis();
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

            // Delete existing refresh tokens for user to avoid accumulating expired tokens
            refreshTokenRepository.deleteByUser(user);

            String accessToken = jwtUtils.generateToken(user.getEmail());
            String refreshToken = createRefreshToken(user);

            AuthResponse response = buildAuthResponse(user, accessToken, refreshToken);
            metricsService.incrementLoginSuccess();
            return response;
        } catch (Exception ex) {
            metricsService.incrementLoginFailure();
            throw ex;
        } finally {
            metricsService.recordLoginDuration(System.currentTimeMillis() - startTime);
        }
    }

    @Transactional
    public TokenRefreshResponse refresh(TokenRefreshRequest request) {
        String token = request.getRefreshToken();
        RefreshToken refreshTokenEntity = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

        if (refreshTokenEntity.getRevoked()) {
            throw new UnauthorizedException("Refresh token has been revoked");
        }

        if (refreshTokenEntity.getExpiryDate().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(refreshTokenEntity);
            throw new UnauthorizedException("Refresh token has expired. Please login again.");
        }

        User user = refreshTokenEntity.getUser();
        
        // Token rotation: delete old refresh token, generate new access and refresh tokens
        refreshTokenRepository.delete(refreshTokenEntity);

        String newAccessToken = jwtUtils.generateToken(user.getEmail());
        String newRefreshToken = createRefreshToken(user);

        return TokenRefreshResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

    @Transactional
    public void logout(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("User not found"));
        refreshTokenRepository.deleteByUser(user);
        SecurityContextHolder.clearContext();
    }

    private String createRefreshToken(User user) {
        String token = UUID.randomUUID().toString();
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(token)
                .expiryDate(LocalDateTime.now().plusDays(refreshExpirationDays))
                .build();
        refreshTokenRepository.save(refreshToken);
        return token;
    }

    private AuthResponse buildAuthResponse(User user, String accessToken, String refreshToken) {
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(AuthResponse.UserInfo.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .email(user.getEmail())
                        .role(user.getRole().name())
                        .build())
                .build();
    }
}
