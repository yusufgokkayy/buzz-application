package com.buzz.auth.service;

import com.buzz.auth.dto.AuthResponse;
import com.buzz.auth.dto.LoginRequest;
import com.buzz.auth.dto.RegisterRequest;
import com.buzz.auth.security.JwtService;
import com.buzz.common.exception.DuplicateResourceException;
import com.buzz.common.exception.ResourceNotFoundException;
import com.buzz.user.entity.User;
import com.buzz.user.enums.Role;
import com.buzz.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Bu email adresi zaten kullanımda.");
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Bu kullanıcı adı zaten kullanımda.");
        }

        User user = User.builder()
                .username(request.getUsername())
                .name(request.getName())
                .surname(request.getSurname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();

        userRepository.save(user);

        String token = jwtService.generateToken(user);

        return buildAuthResponse(user, token, "Kayıt başarılı! Buzz'a hoş geldin ⚡");
    }

    @Override
    public AuthResponse login(LoginRequest request) {

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Email veya şifre hatalı");
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Kullanıcı bulunamadı"));

        String token = jwtService.generateToken(user);

        return buildAuthResponse(user, token, "Giriş başarılı! Tekrar hoş geldin ⚡");
    }

    // ===== Private Helper =====

    private AuthResponse buildAuthResponse(User user, String token, String message) {
        return AuthResponse.builder()
                .token(token)
                .username(user.getUsername())
                .name(user.getName())
                .surname(user.getSurname())
                .email(user.getEmail())
                .role(user.getRole().name())
                .message(message)
                .build();
    }
}
