package com.ikonicit.resource.tracker.controller;

import com.ikonicit.resource.tracker.dto.AttachmentsDTO;
import com.ikonicit.resource.tracker.dto.ViewAttachmentDTO;
import com.ikonicit.resource.tracker.service.AttachmentsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/v1/attachment")
@CrossOrigin(origins = "*", allowedHeaders = "*")
/*@author Parasuram
 *@since 12-05-2021
 */
public class AttachmentsController {

    @Autowired
    AttachmentsService attachmentsService;

    @Operation(summary = "getAttachment by id")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
                    description = "getAttachment",
                    content = {@Content(mediaType = "application/json")}),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404",
                    description = "Page not found",
                    content = @Content)
    })
    @GetMapping(path = "/{id}")
    public ResponseEntity<ViewAttachmentDTO> getAttachment(@PathVariable(value = "id") Integer id) {
        log.info("getAttachment");
        return ResponseEntity.ok(attachmentsService.getAttachment(id));
    }

    @Operation(summary = "getAttachments by id")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
                    description = "getAttachments",
                    content = {@Content(mediaType = "application/json")}),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404",
                    description = "Page not found",
                    content = @Content)
    })
    @GetMapping(path = "/getListAttachments/{id}")
    public ResponseEntity<List<AttachmentsDTO>> getAttachments(@PathVariable(value = "id") Integer id) {
        log.info("getAttachments");
        return ResponseEntity.ok(attachmentsService.getAttachmentsByResourceId(id));
    }

//    @Operation(summary = "Create Attachment Request")
//    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
//            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
//                    description = "Attachment Created Successfully",
//                    content = {@Content(mediaType = "application/json")}),
//            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404",
//                    description = "Page not found",
//                    content = @Content)
//    })
//    @PostMapping
//    public ResponseEntity<String> createAttachment(@RequestParam("attachments") List<MultipartFile> attachments, @RequestParam("id") Integer id) throws ParseException, IOException {
//        log.info("Create Attachment Request");
//                attachmentsService.createAttachment(attachments, id);
//        log.info("Attachment Created Successfully");
//        return new ResponseEntity<>(HttpStatus.OK);
//    }
@Operation(
        summary = "Create Attachment",
        description = "Upload one or more attachments"
)
@PostMapping(value = "/upload", consumes = "multipart/form-data")
public ResponseEntity<String> createAttachment(
        @Parameter(
                description = "List of attachments",
                required = true,
                schema = @Schema(type = "array", implementation = MultipartFile.class)
        )
        @RequestParam("attachments") List<MultipartFile> attachments,

        @Parameter(
                description = "ID of the entity",
                required = true
        )
        @RequestParam("id") Integer id) {
    // Handle the file upload logic using attachments and id
    // Your attachmentsService.createAttachment method can be called here
     attachmentsService.createAttachment(attachments, id);

    // Log success or perform any other actions

    return new ResponseEntity<>("Attachments uploaded successfully", HttpStatus.OK);
}


    @Operation(summary = "Deletes Attachment By Id")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
                    description = "Attachmnet deleted",
                    content = {@Content(mediaType = "application/json")}),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404",
                    description = "Page not found",
                    content = @Content)
    })
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<String> deleteAttachment(@PathVariable(value = "id") Integer id) {
        log.info("deleteAttachment");
        return ResponseEntity.ok(attachmentsService.deleteAttachment(id));
    }

}
