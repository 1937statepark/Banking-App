package com.cs309.websocket3.chat;

import com.cs309.websocket3.Admins.Admin;
import jakarta.persistence.*;
import lombok.Data;
import org.apache.catalina.User;

import java.sql.Time;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "tickets")
@Data
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticketId")
    private Long id;



    @Column
    private String username;

    @Column
    private String body;

    @Column
    private String date;

    @Column
    private String iType;


    @ManyToOne
    @JoinColumn(name = "admin_id", nullable = true)
    private Admin adminId;


    public Ticket(String username, String body, String date, String iType, Admin adminId) {
        this.username = username;
        this.body = body;
        this.date = date;
        this.iType = iType;
        this.adminId = adminId;
    }

    public Ticket() {

    }



    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getBody() {
        return body;
    }
    public void setBody(String body) {
        this.body = body;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public Admin getAdminId() {
        return adminId;
    }
    public void setAdminId(Admin adminId) {
        this.adminId = adminId;
    }
    public void setiType(String iType) {
        this.iType = iType;
    }
    public String getiType() {
        return iType;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }


}