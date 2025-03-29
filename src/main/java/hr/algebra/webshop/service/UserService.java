package hr.algebra.webshop.service;

import hr.algebra.webshop.dto.ChangePasswordDto;
import hr.algebra.webshop.dto.LoginDto;
import hr.algebra.webshop.dto.RegisterDto;
import hr.algebra.webshop.dto.UserDto;
import hr.algebra.webshop.exceptions.EntityNotFoundException;
import hr.algebra.webshop.model.Role;
import hr.algebra.webshop.model.User;
import hr.algebra.webshop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> new UserDto(
                        user.getUsername(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getEmail(),
                        user.getRole()))
                .toList();
    }

    public Optional<UserDto> getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(user -> new UserDto(
                        user.getUsername(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getEmail(),
                        user.getRole()));

    }

    public boolean registerUser(RegisterDto registerDto) {
        if (userRepository.findByUsername(registerDto.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username is already in use");
        }

        User user = new User();
        user.setUsername(registerDto.getUsername());
        user.setFirstName(registerDto.getFirstName());
        user.setLastName(registerDto.getLastName());
        user.setEmail(registerDto.getEmail());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        user.setRole(Role.USER);

        userRepository.save(user);
        return true;
    }

    public Optional<UserDto> loginUser(LoginDto loginDto) {
        Optional<User> userOptional = userRepository.findByUsername(loginDto.getUsername());

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
                return Optional.of(new UserDto(
                        user.getUsername(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getEmail(),
                        user.getRole()
                ));
            }
        }
        return Optional.empty();
    }

    public boolean validateUser(LoginDto loginDto) {
        User user = userRepository.findByUsername(loginDto.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("User with username " + loginDto.getUsername() + " not found"));

        return passwordEncoder.matches(loginDto.getPassword(), user.getPassword());
    }

    public boolean changePassword(ChangePasswordDto changePasswordDto) {
        User user = userRepository.findByUsername(changePasswordDto.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("User with username " + changePasswordDto.getUsername() + " not found"));

        if (passwordEncoder.matches(changePasswordDto.getOldPassword(), user.getPassword())) {
            user.setPassword(passwordEncoder.encode(changePasswordDto.getNewPassword()));
            userRepository.save(user);
            return true;
        }
        return false;
    }
}
