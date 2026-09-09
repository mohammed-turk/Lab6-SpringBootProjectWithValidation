package com.example.employeemanagementsystem.Model;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Employee {
    @NotEmpty
    @Size(min = 3)
    private String ID;

    @NotEmpty
    @Size(min = 5)
    @Pattern(regexp = "^[a-zA-Z ]+$", message = "Name must contain only letters")
    private String name;

    @Email
    private String email;

    @Pattern(regexp = "^05\\d{8}$", message = "Phone number must start with 05 and be exactly 10 digits")
    private String phoneNumber;

    @NotNull
    @Min(26)
    private int age;

    @NotEmpty
    @Pattern(regexp = "^(supervisor|coordinator)$", message = "Position must be either 'supervisor' or 'coordinator'")
    private String position;


    private boolean onLeave;

    @NotNull
    @PastOrPresent
    private LocalDate hireDate;

    @NotNull
    @Positive
    private int annualLeave;

}
