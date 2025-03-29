package hr.algebra.webshop.mvc_controller;

import hr.algebra.webshop.dto.ChangePasswordDto;
import hr.algebra.webshop.dto.LoginDto;
import hr.algebra.webshop.dto.RegisterDto;
import hr.algebra.webshop.service.JwtService;
import hr.algebra.webshop.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute LoginDto loginDto, Model model) {
        if (userService.validateUser(loginDto)) {
            String token = jwtService.generateToken(loginDto.getUsername());
            model.addAttribute("token", token);
            return "redirect:/";
        }
        model.addAttribute("error", "Invalid username or password");
        return "login";
    }

    @GetMapping("/register")
    public String showRegisterPage() {
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute RegisterDto registerDto, Model model) {
        boolean success = userService.registerUser(registerDto);
        if (success) {
            return "redirect:/auth/login";
        }
        model.addAttribute("error", "Registration failed, username already exists.");
        return "register";
    }

    @GetMapping("/change_password")
    public String showChangePasswordPage() {
        return "change_password";
    }

    @PostMapping("/change_password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordDto changePasswordDto) {
        boolean success = userService.changePassword(changePasswordDto);
        if (success) {
            return ResponseEntity.ok("Password changed successfully.");
        }
        return ResponseEntity.badRequest().body("Password change failed, please check your credentials.");
    }
}
