package com.ikonicit.resource.tracker.service;

import com.ikonicit.resource.tracker.dto.ProjectDTO;
import com.ikonicit.resource.tracker.dto.ResourceNamesRequestDTO;
import com.ikonicit.resource.tracker.dto.ResourceNamesResponseDTO;
import com.ikonicit.resource.tracker.entity.Project;

import java.util.List;

/**
 * @author Parasuram
 * @since 10-03-2021
 */
public interface ProjectService {


    ProjectDTO create(ProjectDTO projectDTO);

    ProjectDTO getProject(Integer id);

    ProjectDTO update(ProjectDTO projectDTO);

    List<ProjectDTO> getProjects();

    boolean deleteProject(Integer id);

    List<ProjectDTO> createProjects(List<ProjectDTO> projects);

    List<ResourceNamesResponseDTO> getDeveloperNames(ResourceNamesRequestDTO resourceNamesRequestDTO);

    List<ProjectDTO> getProjectsByResourceId(Integer id);

    String checkProject(String project);
}
