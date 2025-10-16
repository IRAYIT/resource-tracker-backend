package com.ikonicit.resource.tracker.service;

import com.ikonicit.resource.tracker.dto.ProjectDTO;
import com.ikonicit.resource.tracker.dto.ResourceDTO;
import com.ikonicit.resource.tracker.dto.ResourceNamesRequestDTO;
import com.ikonicit.resource.tracker.dto.ResourceNamesResponseDTO;
import com.ikonicit.resource.tracker.entity.Project;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

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
    Predicate<Object> isNull = Predicates.isNull;

    public ProjectServiceImpl(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Override
    public ProjectDTO create(ProjectDTO projectDTO) {
//        if (projectRepository.existsByName(projectDTO.getName())) {
//            throw new RuntimeException("Project already exists");
//        } else {
            Project project = buildProject(projectDTO);
            Resource resource = new Resource();
            resource.setId(projectDTO.getResourceId());
            project.setResource(resource);
            return buildProjectDTO(projectRepository.save(project));
//        }
    }

    @Override
    public ProjectDTO getProject(Integer id) {
        Project project = null;
        Optional<Project> projectOptional = projectRepository.findById(id);
        if (isNotNull.test(projectOptional) && projectOptional.isPresent()) {
            project = projectOptional.get();
        } else {
            throw new ResourceNotFoundException("Project Not Found");
        }
        return buildProjectDTO(project);
    }

    @Override
    public ProjectDTO update(ProjectDTO projectDTO) {
        getProject(projectDTO.getId());
        return buildProjectDTO(projectRepository.save(buildProject(projectDTO)));
    }

    @Override
    public List<ProjectDTO> getProjects() {
        return buildProjectsDTOList(projectRepository.findByStatusNotOrderByIdDesc("CLOSED"));
    }

    @Override
    public boolean deleteProject(Integer id) {
        Optional<Project> optional =projectRepository.findById(id);
        if(!optional.isPresent()){
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
        projectDTOS.forEach(projectDTO -> {
            projects.add(buildProject(projectDTO));
        });
        return buildProjectDTOList(projectRepository.saveAll(projects));
    }

    @Override
    public List<ResourceNamesResponseDTO> getDeveloperNames(ResourceNamesRequestDTO resourceNamesRequestDTO) {
        return resourceRepository.getDeveloperNames(resourceNamesRequestDTO.getSkill(),resourceNamesRequestDTO.getTechnology(), Constants.TERMINATED);
    }

    @Override
    public List<ProjectDTO> getProjectsByResourceId(Integer id) {
        return buildProjectsDTOList(projectRepository.findByResourceIdAndStatusNot(id,Constants.CLOSED));
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

    private List<ProjectDTO> buildProjectDTOList(List<Project> projects) {

        List<ProjectDTO> projectDTOs = new ArrayList<>();
        projects.forEach(project -> {
            projectDTOs.add(buildProjectDTO(project));
        });
        return projectDTOs;
    }

    private Project buildProject(ProjectDTO projectDTO) {
        Project project = new Project();
        Resource resource = new Resource();
        BeanUtils.copyProperties(projectDTO.getResourceDto(),resource);
        resource.setId(projectDTO.getResourceId());
        project.setResource(resource);
        BeanUtils.copyProperties(projectDTO,project);
        return project;
    }

    private ProjectDTO buildProjectDTO(Project project) {
    	ProjectDTO projectDto=new ProjectDTO();
    	BeanUtils.copyProperties(project, projectDto);
    	ResourceDTO resourceDto=new ResourceDTO();
    	BeanUtils.copyProperties(project.getResource(), resourceDto);
    	projectDto.setResourceDto(resourceDto);
    	return projectDto;
    }

    private List<ProjectDTO> buildProjectsDTOList(List<Project> projects) {

        List<ProjectDTO> projectDTOS = new ArrayList<>();
        projects.forEach(project -> {
            projectDTOS.add(buildProjectDTO(project));
        });
        return projectDTOS;
    }
}
