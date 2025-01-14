package com.cs309.websocket3.Accounts;


import com.cs309.websocket3.Transactions.Transaction;
import com.cs309.websocket3.Transactions.TransactionRepository;
import com.cs309.websocket3.Users.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.transaction.Transactional;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
public class AccountController {

  @Autowired
  UserRepository userRepository;

  @Autowired
  AccountRepository accountRepository;

  private final String success = "{\"message\":\"success\"}";
  private final String failure = "{\"message\":\"failure\"}";
  @Autowired
  private TransactionRepository transactionRepository;

  @Operation(summary = "List all existing accounts")
  @ApiResponse(responseCode = "200", description = "Accounts listed")
  @GetMapping(path = "/accounts")
  List<Account> getAllAccounts()  { return accountRepository.findAll(); }

  @Operation(summary = "List account specified by accountNumber in URL path")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Account found successfully",
          content = {@Content(mediaType = "application/json",
                  schema = @Schema(implementation = Account.class))}),
          @ApiResponse(responseCode = "404", description = "Account not found")
  })
  @GetMapping(path = "/accounts/{accountNumber}")
  Account getAccountByNumber(@PathVariable int accountNumber) {return accountRepository.findByaccountNumber(accountNumber);}

  @Operation(summary = "Create a new account")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Account created successfully",
          content = { @Content(mediaType = "application/json",
          schema = @Schema(implementation = Account.class))}),
          @ApiResponse(responseCode = "400", description = "null input in request body, no account created")
  })
  @PostMapping (path = "/accounts")
  String createAccount(@RequestBody Account account) {
    if (account == null)
      return failure;
    accountRepository.save(account);
  return success;
  }

  @Operation(summary = "Update existing account")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Account updated successfully",
          content = {@Content(mediaType = "application/json",
          schema = @Schema(implementation = Account.class))}),
          @ApiResponse(responseCode = "404", description = "Account not found")
  })
  @PutMapping(path = "/accounts/{num}")
  Account updateAccount(@PathVariable int num, @RequestBody Account request) {
    Account account = accountRepository.findByaccountNumber(num);

    if (account == null) {
      return null;
    }

    account.setAccountType(request.getAccountType());
    account.setAccountName(request.getAccountName());
    account.setBalance(request.getBalance());

    //remove transaction relationship so that account can be changed
    for (Transaction transaction : account.getTransactions()) {
      transaction.setAccount(null);
    }
    transactionRepository.deleteAll(account.getTransactions());
    account.getTransactions().clear();

    accountRepository.save(account);
    return accountRepository.findByaccountNumber(num);
  }

  @Operation(summary = "Delete an existing account")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Account deleted successfully"),
          @ApiResponse(responseCode = "404", description = "Account not found")
  })
  @DeleteMapping(path = "/accounts/{num}")
  String deleteAccount(@PathVariable int num) {
    Account account = accountRepository.findByaccountNumber(num);
    transactionRepository.deleteAll(account.getTransactions());
    accountRepository.delete(account);
    return success;
  }

  //sets a user that can access this account other than the owner, removing any existing relationships in the process
  //Can only set a single user at a time
  @Operation(summary = "Enable and add authorized user to existing account")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Authorized user added successfully"),
          @ApiResponse(responseCode = "404", description = "Account not found"),
          @ApiResponse(responseCode = "400", description = "Invalid authuser")
  })
  @PostMapping(path = "/accounts/{accountnum}/auth/{usernum}")
  String setAuthorizedUser(@PathVariable int accountnum, @PathVariable int usernum) {
    Account account = accountRepository.findByaccountNumber(accountnum);
    if (account == null) {
      return failure;
    }
    account.getAuthorizedUsers().clear();
    User authUser;
      authUser = userRepository.findById(usernum);

      if (authUser == null) {
        return "User with ID: " + usernum + " not found";
      }

      account.addAuthorizedUser(authUser);
      accountRepository.save(account);

    return success;
  }

  //Adds a user to the list of authorized users on this account
  @Operation(summary = "Add an authorized user to an account that already has authorized users enabled")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Authuser added successfully"),
          @ApiResponse(responseCode = "404", description = "Account note found"),
          @ApiResponse(responseCode = "400", description = "invalid user input")
  })
  @Transactional
  @PutMapping(path = "/accounts/{accountnum}/auth/{usernum}")
  String updateAuthorizedUser(@PathVariable int accountnum, @PathVariable int usernum) {
    Account account = accountRepository.findByaccountNumber(accountnum);
    if (account == null) {
      return failure;
    }

    User authUser = userRepository.findById(usernum);
    if (authUser == null) {
      return "User with ID: " + usernum + " not found";
    }

    Hibernate.initialize(account.getAuthorizedUsers());
    account.addAuthorizedUser(authUser);
    accountRepository.save(account);

    return success;
  }

  @Operation(summary = "List all users authorized to the account specified by {num} in URL")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Authorized users listed successfully",
          content = {@Content(mediaType = "application/json")}),
          @ApiResponse(responseCode = "404", description = "Account not found")
  })
  @GetMapping(path = "/accounts/{accountnum}/auth")
  Set<User> getAuthorizedUsers(@PathVariable int accountnum) {
    Account account = accountRepository.findByaccountNumber(accountnum);
    if (account == null) {
      return null;
    }
    return account.getAuthorizedUsers();
  }

  //removes authorized user from list of authorized users if exists. Can only delete one user at a time
  @Operation(summary = "Removes a user from authusers list if it exists, can only delete one user at a time")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "User removed from authusers list successfully"),
          @ApiResponse(responseCode = "404", description = "Account or user not found"),
          @ApiResponse(responseCode = "400", description = "invalid User")
  })
  @DeleteMapping(path = "/accounts/{accountnum}/auth/{usernum}")
  String deleteAuthorizedUser(@PathVariable int accountnum, @PathVariable int usernum) {
    Account account = accountRepository.findByaccountNumber(accountnum);
    if (account == null) {
      return failure;
    }

    User user = userRepository.findById(usernum);
    if (user == null) {
      return "User with ID: " + usernum + " not found";
    }
    if (user.removeAuthAccount(account)) {
      userRepository.save(user);
      return success;}

    return failure;
  }
}