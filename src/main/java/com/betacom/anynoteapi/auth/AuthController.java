package com.betacom.anynoteapi.auth;

import com.betacom.anynoteapi.auth.dto.LoginRequest;
import com.betacom.anynoteapi.auth.dto.AuthResponse;
import com.betacom.anynoteapi.auth.dto.RegisterRequest;
import com.betacom.anynoteapi.auth.dto.RegisterResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequest request) {
        var response = authService.login(request);
        var refreshToken = authService.generateRefreshToken(response.token());
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, refreshToken).body(response);
    }

    @PostMapping("refresh")
    public ResponseEntity<AuthResponse> refreshToken(@CookieValue("refreshToken") String refreshToken) {
        var accessToken = authService.refreshToken(refreshToken);
        var newRefreshToken = authService.generateRefreshToken(refreshToken);
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, newRefreshToken).body(accessToken);
    }

    @PostMapping("register")
    public ResponseEntity<RegisterResponse> register(@RequestBody @Valid RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }
}
