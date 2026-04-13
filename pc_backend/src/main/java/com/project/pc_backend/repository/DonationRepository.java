package com.project.pc_backend.repository;

import com.project.pc_backend.dto.DonationStatus;
import com.project.pc_backend.model.Donation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DonationRepository extends JpaRepository<Donation,Long> {
    boolean existsByReference(String reference);

    @Query("SELECT COALESCE(SUM(d.amount), 0) FROM Donation d WHERE d.status = 'SUCCESS'")
    Double getTotalDonations();
    Page<Donation> findByStatusOrderByCreatedAtDesc(DonationStatus status, Pageable pageable);
}
