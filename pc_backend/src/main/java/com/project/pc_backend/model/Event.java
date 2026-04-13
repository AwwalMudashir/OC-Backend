package com.project.pc_backend.model;

import com.project.pc_backend.util.StringListJsonConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(length = 10000)
    private String description;

    private String location;

    private LocalDateTime eventDate;

    @Convert(converter = StringListJsonConverter.class)
    @Column(name = "image_url", columnDefinition = "TEXT")
    private List<String> imageUrls;

    private String videoLink;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public String getImageUrl() {
        if (imageUrls == null || imageUrls.isEmpty()) {
            return null;
        }

        return imageUrls.get(0);
    }
}
