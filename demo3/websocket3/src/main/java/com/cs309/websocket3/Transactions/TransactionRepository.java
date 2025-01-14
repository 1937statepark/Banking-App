package com.cs309.websocket3.Transactions;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long>{
  Transaction findByTransactionNumber(int transactionNumber);
}
