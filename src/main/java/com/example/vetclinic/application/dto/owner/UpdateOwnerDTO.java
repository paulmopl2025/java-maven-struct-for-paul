package com.example.vetclinic.application.dto.owner;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UpdateOwnerDTO {
    private String firstName;
    private String lastName;
    private String phone;

    @Email(message = "Invalid email format")
    private String email;
}
