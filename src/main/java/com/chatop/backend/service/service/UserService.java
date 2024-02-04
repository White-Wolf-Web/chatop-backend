package com.chatop.backend.service.service;

import com.chatop.backend.dto.AuthResponseDto;
import com.chatop.backend.dto.AuthLoginDto;
import com.chatop.backend.dto.AuthRegisterDto;
import com.chatop.backend.dto.UserDto;
import com.chatop.backend.model.User;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;

public interface UserService {
    UserDto registerUser(AuthRegisterDto registerDto);
    UserDto findUserById(Long id);
    List<UserDto> findAllUsers();
    Optional<User> findByEmail(String email);
    UserDto getCurrentUser();
    AuthResponseDto login(AuthLoginDto loginDto);
}
