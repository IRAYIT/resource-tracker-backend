package com.ikonicit.resource.tracker.repository;

import com.ikonicit.resource.tracker.entity.ApplicationTracking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ApplicationTrackingRepository extends JpaRepository<ApplicationTracking, Long> {
    Optional<ApplicationTracking> findByToken(String token);
}