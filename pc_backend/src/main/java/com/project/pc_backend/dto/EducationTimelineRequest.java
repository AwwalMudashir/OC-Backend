package com.project.pc_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EducationTimelineRequest {
    private String period;

    private Long startYear;
    private Long endYear;
    private String title;
    private String qualification;
}
