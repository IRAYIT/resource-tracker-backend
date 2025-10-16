package com.ikonicit.resource.tracker.repository;

import com.ikonicit.resource.tracker.dto.ResourceNamesRequestDTO;
import com.ikonicit.resource.tracker.dto.ResourceNamesResponseDTO;
import com.ikonicit.resource.tracker.entity.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Integer> {
    //@Lock(LockModeType.PESSIMISTIC_READ)
    //@QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "3000")})
    List<Resource> findByManagerIdAndStatusNotOrderByIdDesc(Integer managerId, String status);

    List<Resource> findByManagerIdAndStatusOrderByIdDesc(Integer managerId, String status);

    List<Resource> findAllByManagerIdOrderByIdDesc(Integer managerId);

    List<Resource> findAllByPermissionIdEqualsAndStatusEquals(Integer id, String status);

    Resource findByResourceName(String resourceName);

    Resource findByEmail(String email);

    @Query(value = "select r.email from Resource r where r.status != :status", nativeQuery = true)
    List<String> findEmails(@Param("status") String status);

    @Query(value = " Select new com.ikonicit.resource.tracker.dto.ResourceNamesResponseDTO(r.id,r.resourceName)  from Resource r " +
            "where (r.skill like %?1% or r.technology like %?2%) and r.status != ?3 order by r.resourceName")
    List<ResourceNamesResponseDTO> getDeveloperNames(String skill, String resourceName, String status);

    @Query(value = "SELECT * FROM resource r WHERE r.manager_id IS NULL AND r.permission_id = 3", nativeQuery = true)
    List<Resource> findByManagerIdIsNull();
}
