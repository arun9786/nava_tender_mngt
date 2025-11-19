package com.example.tender.model;

import javax.persistence.*;
@Entity
@Table(name = "users")
public class UserModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String username;
    private String companyName;
    private String password;
    @Column(unique = true, nullable = false)
    private String email;
    @OneToOne
    @JoinColumn(name = "role", referencedColumnName = "id")
    private RoleModel role;

    public UserModel() {
    }

    public UserModel(Integer id, String username, String companyName, String password, String email, RoleModel role) {
        this.id = id;
        this.username = username;
        this.companyName = companyName;
        this.password = password;
        this.email = email;
        this.role = role;
    }

    public UserModel(int id, String username, String password,String email, RoleModel role) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public UserModel(String username, String password,String email, RoleModel role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public UserModel(String password,String email) {
        this.email = email;
        this.password = password;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public RoleModel getRole() { return role; }
    public void setRole(RoleModel role) { this.role = role; }
}
