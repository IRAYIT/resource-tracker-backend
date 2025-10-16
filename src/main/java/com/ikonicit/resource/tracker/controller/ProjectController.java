package com.ikonicit.resource.tracker.controller;

import com.ikonicit.resource.tracker.dto.ProjectDTO;
import com.ikonicit.resource.tracker.dto.ResourceNamesRequestDTO;
import com.ikonicit.resource.tracker.dto.ResourceNamesResponseDTO;
import com.ikonicit.resource.tracker.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/v1/projects")
@CrossOrigin(origins = "*", allowedHeaders = "*")
/**
 * Resources rest services implementation for add,update,get,getAll and delete operations.
 * @author Parasuram
 * @since 21-10-2020
 */
public class ProjectController {

    @Autowired
    ProjectService projectService;

    /**
     * Creates new project.
     * @param projectDTO   it is string representation of create project request body with fields
     * @return ResponseEntity<ProjectDTO> after creates the project
     */
    @Operation(summary = " Creates new project")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
                    description = " creates the project",
                    content = {@Content(mediaType = "application/json")}),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404",
                    description = "Page not found",
                    content = @Content)
    })
    @PostMapping("/createproject")
    public ResponseEntity<ProjectDTO> saveProject(@RequestBody ProjectDTO projectDTO) {
        log.info("Save Project Request");
        return ResponseEntity.ok(projectService.create(projectDTO));
    }

    /**
     * Updates Existing project.
     * @param projectDTO   it is string representation of create project request body with fields
     * @return ResponseEntity<ProjectDTO> after updates the project
     */
    @Operation(summary = "Updates Existing project")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
                    description = "updates the project",
                    content = {@Content(mediaType = "application/json")}),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404",
                    description = "Page not found",
                    content = @Content)
    })
    @PutMapping
    public ResponseEntity<ProjectDTO> updateProject(@RequestBody final ProjectDTO projectDTO) {
        log.info("Update Project Request");
        return ResponseEntity.ok(projectService.update(projectDTO));
    }

    /**
     * Creates multiple project.
     * @param projectDTO   it is string representation of create project request body with fields
     * @return ResponseEntity<List<ProjectDTO>> after creates the projects
     */
    @Operation(summary = " Creates multiple project")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
                    description = "creates the projects",
                    content = {@Content(mediaType = "application/json")}),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404",
                    description = "Page not found",
                    content = @Content)
    })
    @PostMapping("/list")
    public ResponseEntity<List<ProjectDTO>> saveProjects(@RequestBody final List<ProjectDTO> projectDTO) {
        log.info("Save Projects Request");
        return ResponseEntity.ok(projectService.createProjects(projectDTO));
    }

    /**
     * Get the resource by id.
     *
     * @return ResponseEntity<List < OpeningsDTO>> returns the resource for the given resource id
     */
    @Operation(summary = " Get the Projects")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
                    description = "Get projects",
                    content = {@Content(mediaType = "application/json")}),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404",
                    description = "Page not found",
                    content = @Content)
    })
    @GetMapping("/list")
    public ResponseEntity<List<ProjectDTO>> getProjects() {
        log.info("getProjects");
        return ResponseEntity.ok(projectService.getProjects());
    }

    /**
     * Get the Project by id.
     *
     * @return ResponseEntity<ProjectDTO> returns the project for the given project id
     */
    @Operation(summary = "Get the Project by id.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
                    description = " project for the given project id",
                    content = {@Content(mediaType = "application/json")}),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404",
                    description = "Page not found",
                    content = @Content)
    })
    @GetMapping(path = "/{id}")
    public ResponseEntity<ProjectDTO> getProject(@PathVariable(value = "id") final Integer id) {
        log.info("getProject",id);
        return ResponseEntity.ok(projectService.getProject(id));
    }

    /**
     * Get the Resource Names by Skills and Technology.
     *
     * @return ResponseEntity<ProjectDTO> returns the project for the given project id
     */
    @Operation(summary = "Get the Resource Names by Skills and Technology.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
                    description = " project for the given project id",
                    content = {@Content(mediaType = "application/json")}),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404",
                    description = "Page not found",
                    content = @Content)
    })
    @PostMapping("/getResourceNames")
    public ResponseEntity<List<ResourceNamesResponseDTO>> getDeveloperNames(@RequestBody final ResourceNamesRequestDTO resourceNamesRequestDTO ) {
        log.info("Get Resource Names");
        return ResponseEntity.ok(projectService.getDeveloperNames(resourceNamesRequestDTO));
    }

    /**
     * Get the All Projects by ResourceId.
     *
     * @return ResponseEntity<ProjectDTO> returns the project for the given project id
     */
    @Operation(summary = " Get the All Projects by ResourceId")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
                    description = " project for the given project id",
                    content = {@Content(mediaType = "application/json")}),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404",
                    description = "Page not found",
                    content = @Content)
    })
    @GetMapping(path = "getProjectsByResourceId/{id}")
    public ResponseEntity<List<ProjectDTO>> getProjectsByResourceId(@PathVariable(value = "id") final Integer id) {
        log.info("Get Projects By ResourceId",id);
        return ResponseEntity.ok(projectService.getProjectsByResourceId(id));
    }

    /**
     * Deletes the Project record.
     * @param id the id of the Project
     * @return ResponseEntity<Boolean> returns true for project deletion
     */
    @Operation(summary = " Deletes the Project record.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
                    description = "true for project deletion",
                    content = {@Content(mediaType = "application/json")}),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404",
                    description = "Page not found",
                    content = @Content)
    })
    @DeleteMapping("{id}")
    public ResponseEntity<Boolean> deleteProject(@PathVariable(value = "id") final Integer id) {
        log.info("Project Deletion Request");
        return ResponseEntity.ok(projectService.deleteProject(id));
    }

    @GetMapping(path = "projectCheck/{project}")
    public ResponseEntity<String> resourceCheckByProject(@PathVariable(value = "project") final String project) {
        log.info("resourceCheckByProject");
        return ResponseEntity.ok(projectService.checkProject(project));
    }

}
