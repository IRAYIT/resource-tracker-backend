package com.ikonicit.resource.tracker.repository;

import com.ikonicit.resource.tracker.entity.LeaveType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeaveTypeRepository extends JpaRepository<LeaveType, Integer> {
}
