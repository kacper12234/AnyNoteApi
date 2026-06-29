package com.betacom.anynoteapi.auth;

import com.betacom.anynoteapi.auth.dto.LoginRequest;
import com.betacom.anynoteapi.auth.dto.AuthResponse;
import com.betacom.anynoteapi.auth.dto.RegisterRequest;
import com.betacom.anynoteapi.auth.dto.RegisterResponse;
import com.betacom.anynoteapi.exceptions.InvalidTokenTypeException;
import com.betacom.anynoteapi.exceptions.UnauthorizedException;
import com.betacom.anynoteapi.exceptions.UserExistsException;
import com.betacom.anynoteapi.security.JwtService;
import com.betacom.anynoteapi.user.User;
import com.betacom.anynoteapi.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
class AuthService {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    RegisterResponse register(RegisterRequest request) {
        if (userRepository.existsByLogin(request.login())) {
            throw new UserExistsException(request.login());
        }

        var user = new User();
        user.setLogin(request.login());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setCreatedAt(Instant.now());

        var saved = userRepository.save(user);
        return new RegisterResponse(saved.getId(), saved.getLogin(), saved.getCreatedAt());
    }

    AuthResponse login(LoginRequest request) {
        User user = userRepository.findByLogin(request.login()).orElseThrow(UnauthorizedException::new);

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new UnauthorizedException();
        }

        return new AuthResponse(jwtService.generateAccessToken(user.getLogin()), jwtService.getAccessExpiration());
    }

    AuthResponse refreshToken(String refreshToken) {
        if (!jwtService.isRefreshToken(refreshToken)) {
            throw new InvalidTokenTypeException();
        }
        String login = jwtService.extractLogin(refreshToken);
        return new AuthResponse(jwtService.generateAccessToken(login), jwtService.getAccessExpiration());
    }

    String generateRefreshToken(String token) {
        String login = jwtService.extractLogin(token);
        return ResponseCookie.from("refreshToken", jwtService.generateRefreshToken(login))
                .httpOnly(true)
                .path("/")
                .sameSite("Lax")
                .build()
                .toString();
    }
}
