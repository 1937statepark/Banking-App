package com.cs309.websocket3.Transactions;

import com.cs309.websocket3.Accounts.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TransactionController {
  @Autowired
  TransactionRepository transactionRepository;

  @Autowired
  AccountRepository accountRepository;


  private final String success = "{\"message\":\"success\"}";
  private final String failure = "{\"message\":\"failure\"}";

  @Operation(summary = "List all transactions")
  @ApiResponse(responseCode = "200", description = "Transactions listed")
  @GetMapping(path = "/transactions")
  List<Transaction> getAllTransactions() {
    return transactionRepository.findAll();
  }

  @Operation(summary = "Create a new transaction for the account specified by {num} in the URL")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Transaction created successfully"),
          @ApiResponse(responseCode = "400", description = "Invalid Transaction format"),
          @ApiResponse(responseCode = "404", description = "Account not found")
  })
  @PostMapping(path = "/transactions/{num}")
  String createTransaction(@RequestBody Transaction transaction, @PathVariable int num) {
    if (transaction == null) {
      return failure;
    }
    Account account = accountRepository.findByaccountNumber(num);

    if (account == null) {return failure;}
    transaction.setAccount(account);

    double change = transaction.getTransactionAmount();

    if (account.getBalance() - change > 0) {
      account.setBalance(account.getBalance() - change);
      account.addTransaction(transaction);
    } else {
      return failure + "insufficient funds";
    }
    transaction.setResultingBalance(account.getBalance());
    transactionRepository.save(transaction);
    return success;
  }

  /*Meant for admins to remove a transaction in case of fraud or accident
  @Operation(summary = "Deletes a transaction and undoes the change to the balance as a result of the transaction NOT YET IMPLEMENTED")
  @DeleteMapping(path = "/transactions/{num}")
  String rejectTransaction(@PathVariable int num) {
    Transaction transaction = transactionRepository.findByTransactionNumber(num);

    // reverse changes to account balance before deleting transaction from DB
    // planned method removed in favor of having admins post a new transaction to undo changes
    // so that we don't have to update many transaction's resulting balances



    transactionRepository.delete(transaction);
    return success;
  }
  */

  @Operation(summary = "Deletes a transaction without undoing the change made to the balance as a result of the transaction")
  @DeleteMapping(path = "/transactions/{num}/force")
  String forceDeleteTransaction(@PathVariable int num) {
    Transaction transaction = transactionRepository.findByTransactionNumber(num);
    transactionRepository.delete(transaction);
    return success;
  }
}
