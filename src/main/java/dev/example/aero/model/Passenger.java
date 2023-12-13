package dev.example.aero.model;

import dev.example.aero.model.Enumaration.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "PASSENGER", uniqueConstraints = {
        @UniqueConstraint(name = "unique_contact", columnNames = "CONTACT_NO"),
        @UniqueConstraint(name = "unique_email", columnNames = "EMAIL")
})
public class Passenger implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PASSENGER_ID")
    private Long id;

    @NotBlank(message = "First Name is required")
    @Column(name = "FIRST_NAME", nullable = false)
    private String firstName;

    @NotBlank(message = "Last Name is required")
    @Column(name = "LAST_NAME", nullable = false)
    private String lastName;

    @NotEmpty(message = "Provide your contact number")
    @Column(name = "CONTACT_NO", nullable = false)
    @Pattern(regexp = "^(01\\d{9}|1\\d{9})$", message = "Invalid phone number format")
    private String contactNo;

    @NotEmpty(message = "Email Address is required")
    @Email(message = "The Email is not a valid email")
    @Column(name = "EMAIL", nullable = false)
    private String email;

    @NotBlank(message = "The password is required.")
    @Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!*()]).{8,}$", message = "Password must be 8 characters long and combination of uppercase letters, lowercase letters, numbers, special characters.")
    @Column(name = "PASSWORD", nullable = false)
    private String password;

    @Column(name = "DISTANCE_FLIED_KM")
    private double distanceFlied;

    @Enumerated(EnumType.STRING)
    private Role role;
}
