package com.ikonicit.resource.tracker.controller;

import com.ikonicit.resource.tracker.dto.OpeningsDTO;
import com.ikonicit.resource.tracker.service.OpeningsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/v1/openings")
@CrossOrigin(origins = "*", allowedHeaders = "*")
/**
 * Resources rest services implementation for add,update,get,getAll and delete operations.
 * @author Parasuram
 * @since 21-10-2020
 */
public class OpeningsController {

    @Autowired
    OpeningsService openingsService;

    /**
     * Creates new Opening.
     * <p>
     * @param openingsDTO     it is string representation of create Opening request body with fields
     * @return ResponseEntity<OpeningsDTO> after creates the resource
     */
    @Operation(summary = "Creates new Opening.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
                    description = "creates the opening",
                    content = {@Content(mediaType = "application/json")}),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404",
                    description = "Page not found",
                    content = @Content)
    })
    @PostMapping
    public ResponseEntity<OpeningsDTO> saveOpening(@RequestBody final OpeningsDTO openingsDTO) {
        log.info("Save Opening Request");
        return ResponseEntity.ok(openingsService.create(openingsDTO));
    }

    /**
     * Updates the Existing Opening.
     * @param openingsDTO     it is string representation of create Opening request body with fields
     * @return ResponseEntity<OpeningsDTO> after creates the resource
     */
    @Operation(summary = "Updates the Existing Opening")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
                    description = "Opening updated",
                    content = {@Content(mediaType = "application/json")}),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404",
                    description = "Page not found",
                    content = @Content)
    })
    @PutMapping
    public ResponseEntity<OpeningsDTO> updateOpening(@RequestBody final OpeningsDTO openingsDTO) {
        log.info("Update Opening Request");
        return ResponseEntity.ok(openingsService.update(openingsDTO));
    }

    /**
     * Creates Multiple Openings.
     * @param openingsDTO it is string representation of create Opening request body with fields
     * @return ResponseEntity<list<OpeningsDTO>> after creates the resource
     */
    @Operation(summary = "Creates Multiple Openings")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
                    description = "Openings Created",
                    content = {@Content(mediaType = "application/json")}),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404",
                    description = "Page not found",
                    content = @Content)
    })
    @PostMapping("/list")
    public ResponseEntity<List<OpeningsDTO>> saveOpenings(@RequestBody final List<OpeningsDTO> openingsDTO) {
        log.info("Save Opening Request");
        return ResponseEntity.ok(openingsService.createOpenings(openingsDTO));
    }

    /**
     * Get the Openings.
     * @return ResponseEntity<List<OpeningsDTO>> return openings
     */
    @Operation(summary = "Get the Openings.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
                    description = " Openings",
                    content = {@Content(mediaType = "application/json")}),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404",
                    description = "Page not found",
                    content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<OpeningsDTO>> getOpenings() {
        log.info("getOpenings");
        return ResponseEntity.ok(openingsService.getOpenings());
    }

    /**
     * Get the Opening by id.
     * @return ResponseEntity<OpeningsDTO>> returns the opening for the given opening id
     */
    @Operation(summary = "Get the Opening by id")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
                    description = "opening for the given opening id",
                    content = {@Content(mediaType = "application/json")}),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404",
                    description = "Page not found",
                    content = @Content)
    })
    @GetMapping(path = "/{id}")
    public ResponseEntity<OpeningsDTO> getOpening(@PathVariable(value = "id") final Integer id) {
        log.info("getOpening");
        return ResponseEntity.ok(openingsService.getOpening(id));
    }

    /**
     * Deletes Opening by id.
     * @return ResponseEntity<String>  returns String for opening deletion
     */
    @Operation(summary = "Deletes Opening by id")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
                    description = "tring for opening deletion",
                    content = {@Content(mediaType = "application/json")}),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404",
                    description = "Page not found",
                    content = @Content)
    })
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<String> deleteOpening(@PathVariable(value = "id") final Integer id) {
        log.info("deleteOpening");
        return ResponseEntity.ok(openingsService.deleteOpening(id));
    }

}
