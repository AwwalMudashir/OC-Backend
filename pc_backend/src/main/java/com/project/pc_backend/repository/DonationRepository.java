package com.project.pc_backend.repository;

import com.project.pc_backend.model.Donation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DonationRepository extends JpaRepository<Donation,Long> {
    boolean existsByReference(String reference);
}
