package com.buzz.auth.service;

import com.buzz.auth.dto.AuthResponse;
import com.buzz.auth.dto.LoginRequest;
import com.buzz.auth.dto.RegisterRequest;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login (LoginRequest request);
}
