package com.adrassad.cryprice.auth.controller;

import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    private static class UserService {

        public UserService() {
        }

        private User register(UserDto userDto) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        private User authenticate(UserDto userDto) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    private static class JwtUtil {

        public JwtUtil() {
        }

        private String generateToken(String username) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    private static class UserDto {

        public UserDto() {
        }
    }
}