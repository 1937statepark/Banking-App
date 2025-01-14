package com.cs309.websocket3.Accounts;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long>{

  Account findByaccountNumber(int accountNumber);
}
