package com.buzz.user.service;

import com.buzz.user.dto.UpdateUserRequest;
import com.buzz.user.dto.UserResponse;

public interface UserService {

    UserResponse getMyProfile(String email);

    UserResponse getUserById(Long id);

    UserResponse updateMyProfile(String email, UpdateUserRequest request);

    void deleteUser(Long id);
}
