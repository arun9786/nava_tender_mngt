package com.example.tender.service;

import com.example.tender.model.UserModel;
import com.example.tender.repository.RoleRepository;
import com.example.tender.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    public UserModel getUserByEmail(String email){
        return userRepository.findByEmail(email).orElse(null);
    }
}
