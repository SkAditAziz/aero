package dev.example.aero.security.dto;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationResponse {
    private String token;
}