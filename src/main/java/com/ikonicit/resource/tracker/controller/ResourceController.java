package com.ikonicit.resource.tracker.controller;

import com.ikonicit.resource.tracker.dto.ResourceDTO;
import com.ikonicit.resource.tracker.dto.SendEmailDTO;
import com.ikonicit.resource.tracker.service.ResourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.mail.internet.AddressException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/v1/resource")
@CrossOrigin(origins = "*", allowedHeaders = "*")
/**
 * Resources rest services implementation for add,update,get,getAll and delete operations.
 * @author Parasuram
 * @since 21-10-2020
 */
public class ResourceController {

    @Autowired
    ResourceService resourceService;

    /**
     * Creates new resource.
     *
     * @param attachments it can be CV or Cover Letter or any document
     * @param payload     it is string representation of createResource request body with fields
     * @return ResponseEntity<ResourceDTO> after creates the resource
     * @throws ParseException
     */
//    @Operation(summary = " Creates new resource")
//    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
//            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
//                    description = "creates the resource",
//                    content = {@Content(mediaType = "application/json")}),
//            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404",
//                    description = "Page not found",
//                    content = @Content)
//    })
//    @PostMapping
//    public ResponseEntity<ResourceDTO> createResource(@RequestParam("attachments") final List<MultipartFile> attachments, @RequestParam("payload") final String payload) throws ParseException, IOException, AddressException {
//        log.info("Create Resource Request");
//        return ResponseEntity.ok(resourceService.create(attachments, payload));
//    }
    @Operation(
            summary = "Create Resource",
            description = "Upload one or more attachments"
    )
    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public ResponseEntity<ResourceDTO> createResource(
            @Parameter(
                    description = "List of attachments",
                    required = true,
                    schema = @Schema(type = "array", implementation = MultipartFile.class)
            )
            @RequestParam("attachments") List<MultipartFile> attachments,

            @Parameter(
                    description = "Payload",
                    required = true
            )
            @RequestParam("payload") final String payload) throws AddressException, ParseException, IOException {
    System.out.println("calling.....");
        return ResponseEntity.ok(resourceService.create(attachments, payload));
    }




    /**
     * Updates the existing resource.
     *
     * @param attachments it can be CV or Cover Letter or any document
     * @param payload     it is string representation of updateResource request body with fields
     * @return ResponseEntity<ResourceDTO> after update the resource
     * @throws ParseException
     */
//    @Operation(summary = "Updates the existing resource")
//    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
//            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
//                    description = "update the resource",
//                    content = {@Content(mediaType = "application/json")}),
//            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404",
//                    description = "Page not found",
//                    content = @Content)
//    })
//
//    @PutMapping
//    public ResponseEntity<ResourceDTO> updateResource(@RequestParam("attachments") final List<MultipartFile> attachments, @RequestParam("payload") final String payload) throws ParseException {
//        log.info("Update Resource Request");
//        return ResponseEntity.ok(resourceService.update(attachments, payload));
//    }
    @Operation(
            summary = "Update Resource",
            description = "Upload one or more attachments"
    )
    @PutMapping(value = "/update/upload", consumes = "multipart/form-data")
    public ResponseEntity<ResourceDTO> updateResource(
            @Parameter(
                    description = "List of attachments",
                    required = true,
                    schema = @Schema(type = "array", implementation = MultipartFile.class)
            )
            @RequestParam("attachments") List<MultipartFile> attachments,

            @Parameter(
                    description = "Payload",
                    required = true
            )
            @RequestParam("payload") final String payload) throws AddressException, ParseException, IOException {

        return ResponseEntity.ok(resourceService.update(attachments, payload));
    }


    /**
     * Get the resource by id.
     *
     * @param id the resource id
     * @return ResponseEntity<ResourceDTO> returns the resource for the given resource id
     */
    @Operation(summary = "Get the resource by id")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
                    description = "resource for the given resource id ",
                    content = {@Content(mediaType = "application/json")}),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404",
                    description = "Page not found",
                    content = @Content)
    })
    @GetMapping(path = "/{id}")
    public ResponseEntity<ResourceDTO> getResource(@PathVariable(value = "id") final Integer id) {
        log.info("getResourceById");
        return ResponseEntity.ok(resourceService.getResource(id));
    }

    /**
     * Get the resource by name.
     *
     * @param name the resource name
     * @return ResponseEntity<Resource> returns the resource for the given resource name
     */
    @Operation(summary = "Get the resource by name")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
                    description = "resource for the given resource name",
                    content = {@Content(mediaType = "application/json")}),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404",
                    description = "Page not found",
                    content = @Content)
    })
    @GetMapping(path = "byName/{name}")
    public ResponseEntity<ResourceDTO> getResourceByName(@PathVariable(value = "name") final String name) {
        log.info("getResourceByName");
        return ResponseEntity.ok(resourceService.findByResourceName(name));
    }

    /**
     * Check the resource by name.
     *
     * @param name the resource name
     * @return ResponseEntity<String> returns the resource for the given resource name
     */
    @Operation(summary = "Check the resource by name")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
                    description = "resource for the given resource name",
                    content = {@Content(mediaType = "application/json")}),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404",
                    description = "Page not found",
                    content = @Content)
    })
    @GetMapping(path = "nameCheck/{name}")
    public ResponseEntity<String> resourceCheckByName(@PathVariable(value = "name") final String name) {
        log.info("resourceCheckByName");
        return ResponseEntity.ok(resourceService.checkResourceName(name));
    }


    /**
     * Check the resource by name.
     *
     * @param email the resource name
     * @return ResponseEntity<String> returns the resource for the given resource name
     */
    @Operation(summary = "heck the resource by name")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
                    description = "resource for the given resource name",
                    content = {@Content(mediaType = "application/json")}),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404",
                    description = "Page not found",
                    content = @Content)
    })
    @GetMapping(path = "emailCheck/{email}")
    public ResponseEntity<String> resourceCheckByEmail(@PathVariable(value = "email") final String email) {
        log.info("resourceCheckByEmail");
        return ResponseEntity.ok(resourceService.checkEmail(email));
    }

    /**
     * send Emails to resources.
     *
     * @return ResponseEntity<String> returns the resource for the given resource name
     */
    @Operation(summary = "send Emails to resources")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
                    description = "resource for the given resource name",
                    content = {@Content(mediaType = "application/json")}),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404",
                    description = "Page not found",
                    content = @Content)
    })
    @PostMapping("/sendEmail")
    public ResponseEntity<String> sendUpdateEmails(@RequestBody final SendEmailDTO sendEmailDTO) {
        log.info("sendUpdateEmails");
        return ResponseEntity.ok(resourceService.sendEmail(sendEmailDTO));
    }

    /**
     * Get all the resources
     *
     * @return ResponseEntity<List < ResourceDTO> returns the list of resources
     */
    @Operation(summary = "Get all the resources")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
                    description = "list of resources",
                    content = {@Content(mediaType = "application/json")}),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404",
                    description = "Page not found",
                    content = @Content)
    })
    @GetMapping("/list")
    public ResponseEntity<List<ResourceDTO>> getAll() {
        log.info("getAllResources");
        return ResponseEntity.ok(resourceService.getAll());
    }

    /**
     * Deletes the Resource record.
     *
     * @param id the id of the resource
     * @return ResponseEntity<Boolean> returns true for resource deletion
     */
    @Operation(summary = "Deletes the Resource record")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
                    description = "true for resource deletion",
                    content = {@Content(mediaType = "application/json")}),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404",
                    description = "Page not found",
                    content = @Content)
    })
    @DeleteMapping("{id}")
    public ResponseEntity<Boolean> deleteResource(@PathVariable(value = "id") final Integer id) {
        log.info("Resource Deletion Request");
        return ResponseEntity.ok(resourceService.deleteResource(id));
    }

    /**
     * Get the resources by managerId.
     *
     * @param managerId
     * @return ResponseEntity<List < ResourceDTO>> returns the list of resources of a given manager id
     * it returns empty list if no resources found in the database.
     */
    @Operation(summary = "Get the resources by managerId ")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
                    description = "list of resources of a given manager id",
                    content = {@Content(mediaType = "application/json")}),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404",
                    description = "Page not found",
                    content = @Content)
    })
    @GetMapping(path = "/getAllResourcesByManagerId/{managerId}")
    public ResponseEntity<List<ResourceDTO>> getAllResourcesByManager(@PathVariable(value = "managerId") final Integer managerId) {
        log.info("get Resources By managerId");
        return ResponseEntity.ok(resourceService.resourcesByManager(managerId));
    }

    /**
     * Download the attachments by attachmentId.
     *
     * @param attachmentId
     * @return ResponseEntity<byte [ ]> it download the attachments
     */
    @Operation(summary = "Download the attachments by attachmentId")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
                    description = "download the attachments",
                    content = {@Content(mediaType = "application/json")}),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404",
                    description = "Page not found",
                    content = @Content)
    })
    @GetMapping(path = "/attachment/{id}")
    public ResponseEntity<byte[]> getAttachment(@PathVariable(value = "id") final Integer attachmentId) {
        log.info("getAttachmentbyId");
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resourceService.getAttachment(attachmentId).getFileName() + "\"")
                .body(resourceService.getAttachment(attachmentId).getAttachment());
    }

    /**
     * Get all the managers of the sigma company.
     *
     * @return ResponseEntity<List < ResourceDTO>> returns the list of managers
     * it returns empty list if no resources found in the database.
     */
    @Operation(summary = " Get all the managers of the sigma company.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
                    description = " list of managers",
                    content = {@Content(mediaType = "application/json")}),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404",
                    description = "Page not found",
                    content = @Content)
    })
    @GetMapping(value = "/managers")
    public ResponseEntity<List<ResourceDTO>> getAllManagers() {
        log.info("getAllManagers");
        return ResponseEntity.ok(resourceService.getAllManagers());
    }

    @GetMapping(path = "/getAllUnassignedResources")
    public ResponseEntity<List<ResourceDTO>> getAllUnassignedResources() {
        log.info("Fetching all unassigned resources");
        return ResponseEntity.ok(resourceService.getUnassignedResources());
    }

}
