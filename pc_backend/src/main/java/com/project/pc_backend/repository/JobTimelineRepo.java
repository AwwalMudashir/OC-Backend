package com.project.pc_backend.repository;

import com.project.pc_backend.model.JobTimeline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobTimelineRepo extends JpaRepository<JobTimeline,Long> {
}
