package com.socialapp.service.auth;

import com.socialapp.dto.auth.LoginRequestDto;
import com.socialapp.dto.auth.LoginResponseDto;
import com.socialapp.dto.auth.UserDto;

public interface AuthService {
    void signUp(UserDto userDto);
    LoginResponseDto login(LoginRequestDto loginRequestDto);
    void logout(String authHeader);
    UserDto getCurrentUser(String authHeader);
}
