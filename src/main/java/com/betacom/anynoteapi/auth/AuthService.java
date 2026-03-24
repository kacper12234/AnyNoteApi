package com.betacom.anynoteapi.auth;

import com.betacom.anynoteapi.auth.dto.LoginRequest;
import com.betacom.anynoteapi.auth.dto.LoginResponse;
import com.betacom.anynoteapi.auth.dto.RegisterRequest;
import com.betacom.anynoteapi.auth.dto.RegisterResponse;
import com.betacom.anynoteapi.exceptions.UnauthorizedException;
import com.betacom.anynoteapi.exceptions.UserExistsException;
import com.betacom.anynoteapi.security.JwtService;
import com.betacom.anynoteapi.user.User;
import com.betacom.anynoteapi.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public RegisterResponse register(RegisterRequest request) {
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

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByLogin(request.login()).orElseThrow(UnauthorizedException::new);

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new UnauthorizedException();
        }

        return new LoginResponse(jwtService.generateToken(user.getLogin()), jwtService.getExpiration());
    }

    public User getCurrentUser() {
        var username = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        return userRepository.findByLogin(username)
                .orElseThrow(() -> new UsernameNotFoundException("User: " + username + " not found"));
    }


}
