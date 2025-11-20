package com.example.vetclinic.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "owners")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Owner extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    private String phone;

    private String email;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Pet> pets = new HashSet<>();
}
