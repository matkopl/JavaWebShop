package hr.algebra.webshop.controller;

import hr.algebra.webshop.dto.LoginDto;
import hr.algebra.webshop.dto.RegisterDto;
import hr.algebra.webshop.dto.UserDto;
import hr.algebra.webshop.model.User;
import hr.algebra.webshop.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserDto>> getUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserDto> getUserByUsername(@PathVariable String username) {
        return userService.getUserByUsername(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/register")
    public ResponseEntity<UserDto> registerUser(@RequestBody RegisterDto registerDto) {
        User user = userService.registerUser(registerDto);
        if (user == null) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(new UserDto(
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole()
        ));
    }

    @PostMapping("/login")
    public ResponseEntity<UserDto> loginUser(@RequestBody LoginDto loginDto) {
        Optional<UserDto> userDtoOptional = userService.loginUser(loginDto);

        return userDtoOptional.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
