package com.cs309.websocket3.chat;

import com.cs309.websocket3.Admins.Admin;
import com.cs309.websocket3.Admins.AdminRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for managing tickets.
 */
@RestController
@RequestMapping("/tickets")
public class TicketController {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private AdminRepository adminRepository;

    private static final String SUCCESS_MESSAGE = "{\"message\":\"success\"}";
    private static final String FAILURE_MESSAGE = "{\"message\":\"failure\"}";

    /**
     * Retrieve all tickets.
     *
     * @return List of all tickets.
     */
    @Operation(summary = "Retrieve all tickets")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved all tickets")
    @GetMapping
    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    /**
     * Retrieve a ticket by its number.
     *
     * @param ticketNumber The unique number identifying the ticket.
     * @return The ticket if found, otherwise a 404 response.
     */
    @Operation(summary = "Retrieve ticket by number")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ticket retrieved successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Ticket.class))}),
            @ApiResponse(responseCode = "404", description = "Ticket not found")
    })
    @GetMapping("/{ticketNumber}")
    public ResponseEntity<Ticket> getTicketByNumber(@PathVariable int ticketNumber) {
        Ticket ticket = ticketRepository.findById(ticketNumber);
        if (ticket == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(ticket);
    }

    /**
     * Retrieve tickets assigned to a specific admin.
     *
     * @param adminId The ID of the admin.
     * @return List of tickets associated with the admin.
     */
    @Operation(summary = "Retrieve tickets assigned to an admin")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tickets retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Admin not found")
    })
    @GetMapping("/admin/{adminId}")
    public ResponseEntity<List<Ticket>> getTicketsByAdminId(@PathVariable Long adminId) {
        Admin admin = adminRepository.findById(adminId).orElse(null);
        if (admin == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        List<Ticket> tickets = ticketRepository.findByAdminId(admin);
        return ResponseEntity.ok(tickets);
    }

    /**
     * Create a new ticket.
     *
     * @param ticket The ticket to create.
     * @return Response indicating success or failure.
     */
    @Operation(summary = "Create a new ticket")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Ticket created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid ticket data")
    })
    @PostMapping
    public ResponseEntity<String> createTicket(@RequestBody Ticket ticket) {
        if (ticket == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\":\"failure\",\"status\":\"error\"}");
        }
        ticketRepository.save(ticket);
        return ResponseEntity.status(HttpStatus.CREATED).body("{\"message\":\"success\",\"status\":\"created\"}");
    }

    /**
     * Update an existing ticket.
     *
     * @param ticketNumber The number of the ticket to update.
     * @param request      The updated ticket data.
     * @return The updated ticket, or null if not found.
     */
    @Operation(summary = "Update a ticket")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ticket updated successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Ticket.class))}),
            @ApiResponse(responseCode = "404", description = "Ticket not found")
    })
    @PutMapping("/{ticketNumber}")
    public ResponseEntity<Ticket> updateTicket(@PathVariable int ticketNumber, @RequestBody Ticket request) {
        Ticket ticket = ticketRepository.findById(ticketNumber);
        if (ticket == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        ticketRepository.save(request);
        return ResponseEntity.ok(ticketRepository.findById(ticketNumber));
    }

    /**
     * Delete a ticket by its number.
     *
     * @param ticketNumber The number of the ticket to delete.
     * @return Response indicating success or failure.
     */
    @Operation(summary = "Delete a ticket")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ticket deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Ticket not found")
    })
    @DeleteMapping("/{ticketNumber}")
    public ResponseEntity<String> deleteTicket(@PathVariable int ticketNumber) {
        Ticket ticket = ticketRepository.findById(ticketNumber);
        if (ticket == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(FAILURE_MESSAGE);
        }
        ticketRepository.delete(ticket);
        return ResponseEntity.ok(SUCCESS_MESSAGE);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    private static class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String message) {
            super(message);
        }
    }
}
