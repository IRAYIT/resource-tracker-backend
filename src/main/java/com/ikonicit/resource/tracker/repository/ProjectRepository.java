package com.ikonicit.resource.tracker.repository;

import com.ikonicit.resource.tracker.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Integer> {

    List<Project> findByStatusNotOrderByIdDesc(String status);

    // ✅ Updated: traverse project → projectRoles → resources
    @Query("SELECT DISTINCT p FROM Project p " +
            "JOIN p.projectRoles pr " +
            "JOIN pr.resources r " +
            "WHERE r.id = :resourceId AND p.status != :status")
    List<Project> findByResourceIdAndStatusNot(
            @Param("resourceId") Integer resourceId,
            @Param("status") String status
    );

    boolean existsByName(String name);
}