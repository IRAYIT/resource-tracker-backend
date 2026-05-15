package com.ikonicit.resource.tracker.repository;

import com.ikonicit.resource.tracker.entity.Openings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OpeningsRepository extends JpaRepository<Openings, Integer> {
    List<Openings> findByStatusOrderByIdDesc(String status);

    Openings findByName(String name);

    Optional<Openings> findByPublicUrlKey(String publicUrlKey);

    List<Openings> findByStatusIgnoreCase(String terminated);
}
