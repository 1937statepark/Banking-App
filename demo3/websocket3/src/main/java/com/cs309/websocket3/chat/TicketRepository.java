package com.cs309.websocket3.chat;

import com.cs309.websocket3.Admins.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    Ticket findById(int id);

    List<Ticket> findAll();
    List<Ticket> findByAdminId(Admin adminId);
    void delete(Ticket ticket);

}
