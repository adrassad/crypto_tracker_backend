package com.adrassad.cryprice.auth.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.adrassad.cryprice.auth.dto.UserDto;
import com.adrassad.cryprice.auth.model.User;
import com.adrassad.cryprice.auth.security.JwtUtil;
import com.adrassad.cryprice.auth.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public String register(@RequestBody UserDto userDto) {
        User user = userService.register(userDto);
        return "User registered: " + user.getUsername();
    }

    @PostMapping("/login")
    public String login(@RequestBody UserDto userDto) {
        User user = userService.authenticate(userDto);
        return jwtUtil.generateToken(user.getUsername());
    }
}