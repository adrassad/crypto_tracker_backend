package com.adrassad.cryprice.auth.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public User register(UserDto userDto) {
        String hashedPassword = encoder.encode(userDto.getPassword());
        User user = (User) User.builder()
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

    private static class UserRepository {

        public UserRepository() {
        }

        private java.util.Optional<User> findByUsername(String username) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        private User save(User user) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    private static class BCryptPasswordEncoder {

        public BCryptPasswordEncoder() {
        }

        private boolean matches(String password, String password0) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        private String encode(String password) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    private static class UserDto {

        private String username;
        private String password;

        public UserDto() {
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }
    }
}