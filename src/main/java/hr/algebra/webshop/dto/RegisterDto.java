package hr.algebra.webshop.dto;

import lombok.Data;

@Data
public class RegisterDto {
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
}
