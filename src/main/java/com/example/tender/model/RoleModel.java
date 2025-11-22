package com.example.tender.model;

import javax.persistence.*;

@Entity
@Table(name = "roles")
public class RoleModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true)
    private String rolename;

    public RoleModel() {
    }

    public RoleModel(Integer id) {
        this.id = id;
    }

    public RoleModel(String rolename) {
        this.rolename = rolename;
    }

    public RoleModel(Integer id, String rolename) {
        this.id = id;
        this.rolename = rolename;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getRolename() { return rolename; }
    public void setRolename(String rolename) { this.rolename = rolename; }
}
