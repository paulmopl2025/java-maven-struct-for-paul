package com.example.vetclinic.infrastructure.persistence;

import com.example.vetclinic.domain.model.ServiceType;
import com.example.vetclinic.domain.model.VeterinaryService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VeterinaryServiceJpaRepository extends JpaRepository<VeterinaryService, Long> {
    List<VeterinaryService> findByServiceType(ServiceType serviceType);

    List<VeterinaryService> findByActiveTrue();
}
