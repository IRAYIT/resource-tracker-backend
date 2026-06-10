package com.ikonicit.resource.tracker.service;

import com.ikonicit.resource.tracker.dto.*;
import com.ikonicit.resource.tracker.entity.Project;
import com.ikonicit.resource.tracker.entity.ProjectRole;
import com.ikonicit.resource.tracker.entity.Resource;
import com.ikonicit.resource.tracker.exception.ResourceNotFoundException;
import com.ikonicit.resource.tracker.predicates.Predicates;
import com.ikonicit.resource.tracker.repository.ProjectRepository;
import com.ikonicit.resource.tracker.repository.ResourceRepository;
import com.ikonicit.resource.tracker.utils.Constants;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author Parasuram
 */
@Slf4j
@Service
public class ProjectServiceImpl implements ProjectService {

    private ProjectRepository projectRepository;

    @Autowired
    private ResourceRepository resourceRepository;

    Predicate<Object> isNotNull = Predicates.isNotNull;
    Predicate<Object> isNull    = Predicates.isNull;

    public ProjectServiceImpl(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    // ─── CRUD ──────────────────────────────────────────────────────────────────

    @Override
    public ProjectDTO create(ProjectDTO projectDTO) {
        Project project = buildProject(projectDTO);
        return buildProjectDTO(projectRepository.save(project));
    }

    @Override
    public ProjectDTO getProject(Integer id) {
        Optional<Project> projectOptional = projectRepository.findById(id);
        if (isNotNull.test(projectOptional) && projectOptional.isPresent()) {
            return buildProjectDTO(projectOptional.get());
        }
        throw new ResourceNotFoundException("Project Not Found");
    }

    @Override
    public ProjectDTO update(ProjectDTO projectDTO) {
        getProject(projectDTO.getId());
        Project project = buildProject(projectDTO);
        project.setId(projectDTO.getId());
        return buildProjectDTO(projectRepository.save(project));
    }

    @Override
    public List<ProjectDTO> getProjects() {
        return buildProjectsDTOList(
                projectRepository.findByStatusNotOrderByIdDesc("CLOSED")
        );
    }

    @Override
    public boolean deleteProject(Integer id) {
        Optional<Project> optional = projectRepository.findById(id);
        if (!optional.isPresent()) {
            throw new ResourceNotFoundException("Project Not Found in the database");
        }
        Project project = optional.get();
        project.setStatus(Constants.CLOSED);
        projectRepository.save(project);
        return true;
    }

    @Override
    public List<ProjectDTO> createProjects(List<ProjectDTO> projectDTOS) {
        List<Project> projects = new ArrayList<>();
        projectDTOS.forEach(projectDTO -> projects.add(buildProject(projectDTO)));
        return buildProjectDTOList(projectRepository.saveAll(projects));
    }

    // ─── Developer Name Lookup — Industry Standard Skill-Based Matching ────────

    /**
     * Matches employees purely by SKILLS — not by technology label.
     *
     * Why skill-only matching?
     *   A developer's "technology" field is just a primary label (e.g. JAVA).
     *   But a fullstack developer with technology=JAVA may also know React,
     *   Angular, Docker etc. If we filter by technology, we miss these people.
     *
     * How it works:
     *   1. Frontend sends ONE skill per request (e.g. skill="Spring Boot")
     *   2. For each skill, we query employees whose skill column contains it
     *      using 4-pattern word-boundary LIKE (start / middle / end / exact)
     *      This prevents "Java" from matching "JavaScript"
     *   3. We count how many requested skills each employee matched
     *   4. We sort by match count descending — best fit employee appears first
     *
     * Example:
     *   Request skills: ["Spring Boot", "Hibernate", "Microservices"]
     *
     *   Employee A (skill="Java, Spring Boot, Hibernate, Microservices")
     *     → matches all 3 → matchCount = 3 → ranked 1st
     *
     *   Employee B (skill="Java, Spring Boot, React")
     *     → matches 1 → matchCount = 1 → ranked last
     *
     *   Employee C (skill="React, Angular, Spring Boot, Microservices")
     *     technology = REACTJS but knows Spring Boot and Microservices
     *     → matches 2 → matchCount = 2 → ranked 2nd (fullstack dev found!)
     */
    @Override
    public List<ResourceNamesResponseDTO> getDeveloperNames(ResourceNamesRequestDTO dto) {

        String skillString = dto.getSkill();

        if (skillString == null || skillString.trim().isEmpty()) {
            return Collections.emptyList();
        }

        List<String> skills = Arrays.stream(skillString.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .distinct()
                .collect(Collectors.toList());

        Map<Integer, ResourceNamesResponseDTO> dtoMap      = new LinkedHashMap<>();
        Map<Integer, Integer>                  matchCount  = new LinkedHashMap<>();
        Map<Integer, List<String>>             matchedSkills = new LinkedHashMap<>(); // ← new

        skills.forEach(skill -> {
            List<ResourceNamesResponseDTO> results =
                    resourceRepository.findBySkill(skill, Constants.TERMINATED);

            results.forEach(emp -> {
                dtoMap.put(emp.getId(), emp);
                matchCount.merge(emp.getId(), 1, Integer::sum);
                matchedSkills                              // ← track which skills matched
                        .computeIfAbsent(emp.getId(), k -> new ArrayList<>())
                        .add(skill);
            });
        });

        return matchCount.entrySet().stream()
                .sorted(Map.Entry.<Integer, Integer>comparingByValue().reversed())
                .map(entry -> {
                    ResourceNamesResponseDTO empDto = dtoMap.get(entry.getKey());
                    empDto.setMatchCount(entry.getValue());
                    empDto.setMatchedSkills(                // ← set on DTO
                            String.join(", ", matchedSkills.get(entry.getKey()))
                    );
                    return empDto;
                })
                .collect(Collectors.toList());
    }

    // ─── Project-Resource queries ───────────────────────────────────────────────

    @Override
    public List<ProjectDTO> getProjectsByResourceId(Integer id) {
        return buildProjectsDTOList(
                projectRepository.findByResourceIdAndStatusNot(id, Constants.CLOSED)
        );
    }

    @Override
    public String checkProject(String project) {
        List<Project> projectList = projectRepository.findAll();
        for (Project a : projectList) {
            if (a.getName().equals(project)) {
                return "Project Name Already exist";
            }
        }
        return "Project Name Available";
    }

    // ─── Private builders ───────────────────────────────────────────────────────

    private Project buildProject(ProjectDTO projectDTO) {
        Project project = new Project();
        // Exclude projectRoles — mapped manually below to avoid type mismatch
        BeanUtils.copyProperties(projectDTO, project, "projectRoles");

        if (projectDTO.getProjectRoles() != null) {
            List<ProjectRole> roles = projectDTO.getProjectRoles()
                    .stream()
                    .map(roleDTO -> {
                        ProjectRole role = new ProjectRole();
                        role.setRoleLabel(roleDTO.getRoleLabel());
                        role.setTechnology(roleDTO.getTechnology());

                        // Convert List<String> → comma-separated String for DB storage
                        // e.g. ["Spring Boot", "Hibernate"] → "Spring Boot,Hibernate"
                        if (roleDTO.getSkills() != null) {
                            role.setSkills(String.join(",", roleDTO.getSkills()));
                        }

                        // Map List<Integer> resourceIds → List<Resource> entities
                        if (roleDTO.getResourceIds() != null) {
                            List<Resource> resources = roleDTO.getResourceIds()
                                    .stream()
                                    .map(id -> resourceRepository.findById(id)
                                            .orElseThrow(() ->
                                                    new RuntimeException("Resource not found: " + id)))
                                    .collect(Collectors.toList());
                            role.setResources(resources);
                        }

                        // Back-reference to project — required for orphanRemoval to work
                        role.setProject(project);
                        return role;
                    })
                    .collect(Collectors.toList());

            project.setProjectRoles(roles);
        }

        return project;
    }

    private ProjectDTO buildProjectDTO(Project project) {
        ProjectDTO projectDto = new ProjectDTO();
        // Exclude projectRoles — mapped manually below
        BeanUtils.copyProperties(project, projectDto, "projectRoles");

        if (project.getProjectRoles() != null) {
            List<ProjectRoleDTO> roleDTOs = project.getProjectRoles()
                    .stream()
                    .map(role -> {
                        ProjectRoleDTO dto = new ProjectRoleDTO();
                        dto.setId(role.getId());
                        dto.setRoleLabel(role.getRoleLabel());
                        dto.setTechnology(role.getTechnology());

                        // Convert comma-separated String → List<String>
                        // e.g. "Spring Boot,Hibernate" → ["Spring Boot", "Hibernate"]
                        if (role.getSkills() != null && !role.getSkills().isEmpty()) {
                            dto.setSkills(List.of(role.getSkills().split(",")));
                        }

                        // Map List<Resource> → List<Integer> resource ids
                        if (role.getResources() != null) {
                            dto.setResourceIds(
                                    role.getResources()
                                            .stream()
                                            .map(Resource::getId)
                                            .collect(Collectors.toList())
                            );
                            dto.setResourceNames(
                                    role.getResources()
                                            .stream()
                                            .map(Resource::getResourceName)
                                            .collect(Collectors.toList())
                            );
                        }

                        return dto;
                    })
                    .collect(Collectors.toList());

            projectDto.setProjectRoles(roleDTOs);
        }

        return projectDto;
    }

    private List<ProjectDTO> buildProjectDTOList(List<Project> projects) {
        List<ProjectDTO> projectDTOs = new ArrayList<>();
        projects.forEach(project -> projectDTOs.add(buildProjectDTO(project)));
        return projectDTOs;
    }

    private List<ProjectDTO> buildProjectsDTOList(List<Project> projects) {
        List<ProjectDTO> projectDTOS = new ArrayList<>();
        projects.forEach(project -> projectDTOS.add(buildProjectDTO(project)));
        return projectDTOS;
    }
}