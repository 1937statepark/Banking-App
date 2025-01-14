package com.cs309.websocket3.Accounts;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.cs309.websocket3.Transactions.Transaction;
import com.cs309.websocket3.Users.User;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Entity
public class Account {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Getter
  private int accountNumber;

  @Getter @Setter
  private String accountName;

  @Getter @Setter
  private String accountType;

  @Getter @Setter
  private double balance;

  @Getter
  @Setter
  @JsonIgnore
  @ManyToMany(mappedBy = "authAccounts", cascade = { CascadeType.PERSIST, CascadeType.MERGE })
  private Set<User> authorizedUsers;

  @ManyToOne
  @JoinColumn(name = "id")
  @JsonIgnore
  private User user;

  @Getter @Setter
  @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
  @JsonManagedReference
  private List<Transaction> transactions;

  public Account(int accountOwner, String accountName, String accountType, double balance) {
    this.accountName = accountName;
    this.accountType = accountType;
    this.balance = balance;
    authorizedUsers = new HashSet<>();
    transactions = new ArrayList<>();
  }

  public Account() {
    transactions = new ArrayList<>();
    authorizedUsers = new HashSet<>();
  }

  public void addTransaction(Transaction transaction) {
    transactions.add(transaction);
  }

  public void addAuthorizedUser(User user) {
    this.authorizedUsers.add(user);
    user.getAuthAccounts().add(this);
  }
}

