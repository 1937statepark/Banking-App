package com.cs309.websocket3.Users;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller to manage user-related operations.
 * Provides endpoints to create, retrieve, update, and delete users.
 */
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    private static final String SUCCESS_MESSAGE = "{\"message\":\"success\"}";
    private static final String FAILURE_MESSAGE = "{\"message\":\"failure\"}";

    @Operation(summary = "Retrieve all users")
    @ApiResponse(responseCode = "200", description = "List of all users retrieved successfully")
    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Operation(summary = "Find a user by username")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{username}")
    public ResponseEntity<User> findByUsername(@PathVariable String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Create a new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input or null user")
    })
    @PostMapping
    public ResponseEntity<String> createUser(@RequestBody User user) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(FAILURE_MESSAGE);
        }
        userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(SUCCESS_MESSAGE);
    }

    @Operation(summary = "Update an existing user by username")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping("/{username}")
    public ResponseEntity<User> updateUser(@PathVariable String username, @RequestBody User request) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        user.setPassword(request.getPassword());
        userRepository.save(user);
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Delete a user by username")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping("/{username}")
    public ResponseEntity<String> deleteUser(@PathVariable String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(FAILURE_MESSAGE);
        }
        userRepository.deleteByUsername(username);
        return ResponseEntity.ok(SUCCESS_MESSAGE);
    }
}
