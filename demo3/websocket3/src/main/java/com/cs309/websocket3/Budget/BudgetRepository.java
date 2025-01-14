package com.cs309.websocket3.Budget;

import org.springframework.data.jpa.repository.JpaRepository;



public interface BudgetRepository extends JpaRepository<Budget, Long> {
    Budget findById(int id);
}
