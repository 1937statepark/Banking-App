package com.cs309.websocket3.Budget;

import org.springframework.web.bind.annotation.*;
import com.cs309.websocket3.Budget.BudgetRepository;
import com.cs309.websocket3.Budget.Budget;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * Controller for managing budget operations.
 */
@RestController
@RequestMapping("/budget")
public class BudgetController {

    @Autowired
    private BudgetRepository budgetRepository;

    private static final String SUCCESS_MESSAGE = "{\"message\":\"success\"}";
    private static final String FAILURE_MESSAGE = "{\"message\":\"failure\"}";

    /**
     * Retrieve all budgets.
     *
     * @return List of all budgets in the system.
     */
    @Operation(summary = "Retrieve all budgets")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of budgets")
    @GetMapping
    public List<Budget> getAllBudgets() {
        return budgetRepository.findAll();
    }

    /**
     * Retrieve a specific budget by user ID.
     *
     * @param userId The ID of the user.
     * @return Budget associated with the provided user ID.
     */
    @Operation(summary = "Retrieve budget by user ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Budget found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Budget.class))}),
            @ApiResponse(responseCode = "404", description = "Budget not found")
    })
    @GetMapping("/{userId}")
    public ResponseEntity<Budget> getBudgetByUserId(@PathVariable Long userId) {
        Budget budget = budgetRepository.findById(userId).orElse(null);
        if (budget == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(budget);
    }

    /**
     * Create a new budget.
     *
     * @param budget The budget object to create.
     * @return Response indicating success or failure.
     */
    @Operation(summary = "Create a new budget")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Budget created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping
    public ResponseEntity<String> createBudget(@RequestBody Budget budget) {
        if (budget == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(FAILURE_MESSAGE);
        }
        budgetRepository.save(budget);
        return ResponseEntity.status(HttpStatus.CREATED).body(SUCCESS_MESSAGE);
    }

    /**
     * Update the expenses for an existing budget.
     *
     * @param userId    The ID of the user whose budget is being updated.
     * @param newBudget The budget data containing the updated expenses.
     * @return Response indicating success or failure.
     */
    @Operation(summary = "Update budget expenses")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Budget updated successfully"),
            @ApiResponse(responseCode = "404", description = "Budget not found"),
            @ApiResponse(responseCode = "400", description = "Invalid expense data")
    })
    @PutMapping("/{userId}")
    public ResponseEntity<String> updateExpenses(@PathVariable Long userId, @RequestBody Budget newBudget) {
        Budget budget = budgetRepository.findById(userId).orElse(null);

        if (budget == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(FAILURE_MESSAGE);
        }

        try {
            double updatedExpenses = budget.getExpenses() + newBudget.getExpenses();
            budget.setExpenses(updatedExpenses);
            budgetRepository.save(budget);
            double updatedExpensesDif = budget.getExpexp() - updatedExpenses;
            budget.setExpexp(updatedExpensesDif);
            budgetRepository.save(budget);
            return ResponseEntity.ok(SUCCESS_MESSAGE);
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(FAILURE_MESSAGE);
        }
    }
}
