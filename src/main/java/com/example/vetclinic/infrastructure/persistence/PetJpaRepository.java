package com.example.vetclinic.infrastructure.persistence;

import com.example.vetclinic.domain.model.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PetJpaRepository extends JpaRepository<Pet, Long> {
    List<Pet> findByOwnerId(Long ownerId);
}
