package com.project.pc_backend.repository;

import com.project.pc_backend.model.EducationTimeline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EducationTimelineRepo extends JpaRepository<EducationTimeline,Long> {

}
