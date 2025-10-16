package com.ikonicit.resource.tracker.controller;

import com.ikonicit.resource.tracker.dto.ChangePasswordDTO;
import com.ikonicit.resource.tracker.dto.ForgotPasswordDTO;
import com.ikonicit.resource.tracker.dto.LoginDTO;
import com.ikonicit.resource.tracker.dto.ResourceDTO;
import com.ikonicit.resource.tracker.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping("api/v1/user")
@CrossOrigin(origins = "*", allowedHeaders = "*")
/**
 * Implementation of an User rest services includes changePassword,login and
 * forgotPassword methods. method.
 *
 * @author Parasuram
 * @since 12-12-2020
 */
public class UserController {

    @Autowired
    UserService userService;

    /**
     * Change Password for User.
     * @param changePasswordDTO of the User
     * @return ResponseEntity<String> returns String
     */   @Operation(summary = " Change Password for User")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
                    description = "String",
                    content = {@Content(mediaType = "application/json")}),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404",
                    description = "Page not found",
                    content = @Content)
    })

    @PutMapping(path = "changePassword", consumes = "application/json")
    public ResponseEntity<String> changePassword(@RequestBody final ChangePasswordDTO changePasswordDTO) {
        log.info("Change Password");
        return ResponseEntity.ok(userService.changePassword(changePasswordDTO));
    }

    /**
     * Login for User.
     * @param loginDTO of the user
     * @return ResponseEntity<String> returns String
     */
    @Operation(summary = " Login for User")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
                    description = "String",
                    content = {@Content(mediaType = "application/json")}),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404",
                    description = "Page not found",
                    content = @Content)
    })
    @PostMapping(path = "login")
    public ResponseEntity<ResourceDTO> login(@RequestBody final LoginDTO loginDTO) {
        log.info("Employee Login");
        return ResponseEntity.ok(userService.login(loginDTO));
    }

    /**
     * Forgot Password for User.
     * @param forgotPasswordDTO of the user
     * @return ResponseEntity<String> returns String
     */
    @Operation(summary = "  Forgot Password for User")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
                    description = " String",
                    content = {@Content(mediaType = "application/json")}),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404",
                    description = "Page not found",
                    content = @Content)
    })
    @PostMapping(path = "/forgotPassword")
    public ResponseEntity<String> forgotPassword(@Valid @RequestBody final ForgotPasswordDTO forgotPasswordDTO) {
        log.info("Forgot Password");
        return ResponseEntity.ok(userService.forgotPassword(forgotPasswordDTO));
    }
}
