package com.example.tender.controller;

import com.example.tender.dto.LoginDTO;
import com.example.tender.security.JwtUtil;
import com.example.tender.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class LoginController {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private LoginService loginService;
    @Autowired
    private JwtUtil jwtUtil;


    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> authenticateUser(@RequestBody LoginDTO authenticationRequest) throws Exception{
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getEmail(), authenticationRequest.getPassword()));
        } catch (BadCredentialsException e) {
            Map<String, Object> map = new HashMap<>();
            map.put("message", "Invalid credentials");
            return ResponseEntity.badRequest().body(map);
        }

        UserDetails userDetails = loginService.loadUserByUsername(authenticationRequest.getEmail());
        String token = jwtUtil.generateToken(userDetails);
        Map<String, Object> resp = new HashMap<>();
        resp.put("jwt", token);
        resp.put("status", Integer.valueOf(200));
        return ResponseEntity.ok(resp);
    }
}
