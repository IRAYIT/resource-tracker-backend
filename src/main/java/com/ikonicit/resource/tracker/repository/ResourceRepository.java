package com.ikonicit.resource.tracker.repository;

import com.ikonicit.resource.tracker.dto.ResourceNamesResponseDTO;
import com.ikonicit.resource.tracker.entity.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Integer> {

    List<Resource> findByManagerIdAndStatusNotOrderByIdDesc(Integer managerId, String status);

    List<Resource> findByManagerIdAndStatusOrderByIdDesc(Integer managerId, String status);

    List<Resource> findAllByManagerIdOrderByIdDesc(Integer managerId);

    List<Resource> findAllByPermissionIdEqualsAndStatusEquals(Integer id, String status);

    Resource findByResourceName(String resourceName);

    Resource findByEmail(String email);

    @Query("SELECT r.email FROM Resource r WHERE r.status != :status AND r.permission.id IN (2, 3)")
    List<String> findEmails(@Param("status") String status);

    /**
     * Find employees by a SINGLE skill using word-boundary-safe LIKE patterns.
     *
     * Your skill column format: "Java, Spring Boot, Spring MVC, Hibernate"
     *                            (comma + space separated)
     *
     * Four patterns cover all positions a skill can appear in the string:
     *
     *   1. EXACT  — employee has only this one skill
     *      skill = 'Java'  →  r.skill = 'Java'
     *
     *   2. START  — skill is the first in the list
     *      skill = 'Java'  →  r.skill LIKE 'Java,%'
     *      matches: "Java, Spring Boot, Hibernate"
     *
     *   3. MIDDLE — skill is between other skills
     *      skill = 'Spring Boot'  →  r.skill LIKE '%, Spring Boot,%'
     *      matches: "Java, Spring Boot, Hibernate"
     *
     *   4. END    — skill is the last in the list
     *      skill = 'Hibernate'  →  r.skill LIKE '%, Hibernate'
     *      matches: "Java, Spring Boot, Hibernate"
     *
     * Why not just LIKE '%Java%'?
     *   '%Java%' would also match 'JavaScript', 'JavaFX' — wrong results.
     *   These four patterns respect the ", " delimiter in your data.
     *
     * CASE WHEN r.manager IS NOT NULL prevents implicit inner join
     *   which was silently dropping employees with no manager.
     *
     * Does NOT filter by technology — skill matching only.
     * Technology is a UI label, not a filter.
     */
    @Query(value = "SELECT new com.ikonicit.resource.tracker.dto.ResourceNamesResponseDTO(" +
            "r.id, r.resourceName) " +
            "FROM Resource r " +
            "WHERE r.status != :status " +
            "AND (" +
            "    lower(r.skill) = lower(:skill) " +
            " OR lower(r.skill) LIKE lower(concat(:skill, ',%')) " +
            " OR lower(r.skill) LIKE lower(concat('%, ', :skill, ',%')) " +
            " OR lower(r.skill) LIKE lower(concat('%, ', :skill)) " +
            ") " +
            "ORDER BY r.resourceName")
    List<ResourceNamesResponseDTO> findBySkill(
            @Param("skill") String skill,
            @Param("status") String status
    );

    @Query(value = "SELECT * FROM resource r WHERE r.manager_id IS NULL AND r.permission_id = 4",
            nativeQuery = true)
    List<Resource> findByManagerIdIsNull();

    List<Resource> findAllByPermissionIdInAndStatus(List<Integer> permissionIds, String status);

    boolean existsByEmail(String email);
}