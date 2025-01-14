package com.cs309.websocket3.Users;

import com.cs309.websocket3.Accounts.Account;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private String username;
    private String password;
    private String address;
    private String phone;


    // Field to store the ID of the associated Budget
    private Integer budgetId;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Account> accounts;

    //Many-to-many authored by Kenny using https://www.baeldung.com/jpa-many-to-many
    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.EAGER)
    @JoinTable(
            name = "authUsers",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "account_num")
    )
    Set<Account> authAccounts;

    public User(String name, String username, String password, String address, String phone, List<Account> accounts, List<Account> authAccounts) {
        this.name = name;
        this.username = username;
        this.password = password;
        this.address = address;
        this.phone = phone;
        this.accounts = new ArrayList<>();
        this.authAccounts = new HashSet<>();
    }

    public User() { accounts = new ArrayList<>();
    }

    // Getters and Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getBudgetId() {
        return budgetId;
    }

    public void setBudgetId(Integer budgetId) {
        this.budgetId = budgetId;
    }

    public Set<Account> getAuthAccounts() {return authAccounts;}






    public void setAuthAccounts(Set<Account> authAccounts) {this.authAccounts = authAccounts;}

    public void addAuthAccount(Account account) {
        this.authAccounts.add(account);
        account.getAuthorizedUsers().add(this);
    }

    public boolean removeAuthAccount(Account account) {
        account.getAuthorizedUsers().remove(this);
        return this.authAccounts.remove(account);
    }
}
