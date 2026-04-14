package com.project.pc_backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "job_timeline")
public class JobTimeline{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    private String title;

    @Column(name = "job_description", columnDefinition = "TEXT")
    private String desc;

    @Column(name = "done_by")
    private String doneBy;
}