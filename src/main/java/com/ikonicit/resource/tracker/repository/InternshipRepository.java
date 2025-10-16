package com.ikonicit.resource.tracker.repository;

import com.ikonicit.resource.tracker.entity.Internship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InternshipRepository extends JpaRepository<Internship,Integer> {

}
