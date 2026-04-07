package com.buzz.user.service;

import com.buzz.common.exception.DuplicateResourceException;
import com.buzz.common.exception.ResourceNotFoundException;
import com.buzz.user.dto.UpdateUserRequest;
import com.buzz.user.dto.UserResponse;
import com.buzz.user.entity.User;
import com.buzz.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    // ===== PROFİLİMİ GETİR =====

    @Override
    public UserResponse getMyProfile(String email) {
        System.out.println(email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Bu email ile bir kullanıcı yok"));


        return buildUserResponse(user);
    }

    // ===== ID İLE GETİR =====

    @Override
    public UserResponse getUserById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bu id ile bir kullanıcı yok"));

        return buildUserResponse(user);
    }

    // ===== GÜNCELLE =====

    @Override
    @Transactional
    public UserResponse updateMyProfile(String email, UpdateUserRequest request) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Bu email ile bir kullanıcı yok"));

        // Username kontrolü ve güncelleme
        if (request.getUsername() != null) {
            if (!request.getUsername().equals(user.getUsername()) &&
                    userRepository.existsByUsername(request.getUsername())) {
                throw new DuplicateResourceException("Bu kullanıcı adı zaten kullanımda");
            }
            user.setUsername(request.getUsername());
        }

        // Email kontrolü ve güncelleme
        if (request.getEmail() != null) {
            if (!request.getEmail().equals(user.getEmail()) &&
                    userRepository.existsByEmail(request.getEmail())) {
                throw new DuplicateResourceException("Bu email zaten kullanımda");
            }
            user.setEmail(request.getEmail());
        }

        userRepository.save(user);

        return buildUserResponse(user);
    }

    // ===== SİL =====

    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bu id ile bir kullanıcı yok"));
        userRepository.delete(user);
    }

    // ===== HELPER =====

    private UserResponse buildUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().name())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
