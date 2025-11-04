package com.example.tender.repository;

import com.example.tender.model.RoleModel;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<RoleModel, Integer> {
    Optional<RoleModel> findByRolename(String rolename);
}
