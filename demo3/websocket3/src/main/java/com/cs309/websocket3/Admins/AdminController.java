package com.cs309.websocket3.Admins;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import java.util.List;

/**
 * Controller for managing admin accounts.
 */
@RestController
@RequestMapping("/admins")
public class AdminController {

    @Autowired
    private AdminRepository adminRepository;

    private static final String SUCCESS_MESSAGE = "{\"message\":\"success\"}";
    private static final String FAILURE_MESSAGE = "{\"message\":\"failure\"}";

    /**
     * Retrieve all admin accounts.
     *
     * @return List of all admins.
     */
    @Operation(summary = "Retrieve all admin accounts")
    @ApiResponse(responseCode = "200", description = "Admins retrieved successfully")
    @GetMapping
    public List<Admin> getAllAdmins() {
        return adminRepository.findAll();
    }

    /**
     * Retrieve an admin by their employee ID.
     *
     * @param employeeId The unique ID of the admin.
     * @return The admin account if found, or a 404 response otherwise.
     */
    @Operation(summary = "Retrieve an admin by employee ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Admin retrieved successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Admin.class))}),
            @ApiResponse(responseCode = "404", description = "Admin not found")
    })
    @GetMapping("/{employeeId}")
    public ResponseEntity<Admin> getAdminById(@PathVariable int employeeId) {
        Admin admin = adminRepository.findById(employeeId);
        if (admin == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(admin);
    }

    /**
     * Create a new admin account.
     *
     * @param admin The admin details to create.
     * @return Success or failure message.
     */
    @Operation(summary = "Create a new admin account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Admin created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid admin data provided")
    })
    @PostMapping
    public ResponseEntity<String> createAdmin(@RequestBody Admin admin) {
        if (admin == null) {
            return ResponseEntity.badRequest().body(FAILURE_MESSAGE);
        }
        adminRepository.save(admin);
        return ResponseEntity.status(201).body(SUCCESS_MESSAGE);
    }

    /**
     * Update an existing admin account.
     *
     * @param employeeId The ID of the admin to update.
     * @param request    The updated admin data.
     * @return The updated admin account or a 404 response if not found.
     */
    @Operation(summary = "Update an existing admin account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Admin updated successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Admin.class))}),
            @ApiResponse(responseCode = "404", description = "Admin not found")
    })
    @PutMapping("/{employeeId}")
    public ResponseEntity<Admin> updateAdmin(@PathVariable int employeeId, @RequestBody Admin request) {
        Admin admin = adminRepository.findById(employeeId);
        if (admin == null) {
            return ResponseEntity.notFound().build();
        }
        admin.setPassword(request.getPassword());
        adminRepository.save(admin);
        return ResponseEntity.ok(admin);
    }

    /**
     * Delete an admin account by their employee ID.
     *
     * @param employeeId The ID of the admin to delete.
     * @return Success or failure message.
     */
    @Operation(summary = "Delete an admin account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Admin deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Admin not found")
    })
    @DeleteMapping("/{employeeId}")
    public ResponseEntity<String> deleteAdmin(@PathVariable int employeeId) {
        Admin admin = adminRepository.findById(employeeId);
        if (admin == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(FAILURE_MESSAGE);
        }
        adminRepository.deleteById(employeeId);
        return ResponseEntity.ok(SUCCESS_MESSAGE);
    }
}
