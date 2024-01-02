package dev.example.aero.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LoginReqDTO {
    private String username;
    private String password;
}
