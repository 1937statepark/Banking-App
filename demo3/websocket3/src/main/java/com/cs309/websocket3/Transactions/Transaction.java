package com.cs309.websocket3.Transactions;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.cs309.websocket3.Accounts.Account;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "transactions")
public class Transaction {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Getter @Setter
  private int transactionNumber;

  @Getter @Setter
  private double transactionAmount;

  @Getter @Setter
  private String memo;

  //Calculated based on the balance of the account attached to the transaction and the transactionAmount
  @Getter @Setter
  private double resultingBalance;

  //Many to one to allow finding the source account from a transaction
  //Account affected by the transaction
  @Getter @Setter
  @ManyToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "account_num")
  @JsonBackReference
  private Account account;

  public Transaction(){

  }

  private Transaction(double amount, String memo, Account source){
   this.transactionAmount = amount;
   this.memo = memo;
   this.account = source;
  }
}
