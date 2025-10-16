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
   
    List<Project> findByResourceIdAndStatusNot(Integer resourceId,String status);

    boolean existsByName(String name);
}
