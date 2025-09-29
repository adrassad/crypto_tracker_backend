package com.adrassad.cryprice.auth.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.adrassad.cryprice.auth.dto.UserDto;
import com.adrassad.cryprice.auth.model.User;
import com.adrassad.cryprice.auth.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public User register(UserDto userDto) {
        String hashedPassword = encoder.encode(userDto.getPassword());
        User user = User.builder()
                .username(userDto.getUsername())
                .password(hashedPassword)
                .build();
        return userRepository.save(user);
    }

    public User authenticate(UserDto userDto) {
        User user = userRepository.findByUsername(userDto.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (encoder.matches(userDto.getPassword(), user.getPassword())) {
            return user;
        } else {
            throw new RuntimeException("Invalid credentials");
        }
    }
}