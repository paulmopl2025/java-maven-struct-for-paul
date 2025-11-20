package com.example.vetclinic.application.dto.owner;

import lombok.Data;
import java.util.Set;

@Data
public class OwnerDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
}
