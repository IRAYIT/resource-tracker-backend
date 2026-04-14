package com.ikonicit.resource.tracker.entity;

import jakarta.persistence.*;
import lombok.Data;
import jakarta.persistence.Id;
import java.time.LocalDateTime;

@Entity
@Table(name = "application_tracking")
@Data
public class ApplicationTracking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long applicationId;

    @Column(nullable = false, unique = true)
    private String token;

    private LocalDateTime expiryTime;
}
