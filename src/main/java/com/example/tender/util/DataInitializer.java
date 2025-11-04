package com.example.tender.util;

import com.example.tender.model.RoleModel;
import com.example.tender.model.UserModel;
import com.example.tender.repository.RoleRepository;
import com.example.tender.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        RoleModel bidderRole = roleRepository.findByRolename("BIDDER").orElseGet(() -> { RoleModel r = new RoleModel(); r.setRolename("BIDDER"); return roleRepository.save(r); });
        RoleModel approverRole = roleRepository.findByRolename("APPROVER").orElseGet(() -> { RoleModel r = new RoleModel(); r.setRolename("APPROVER"); return roleRepository.save(r); });

        if (userRepository.findByEmail("bidderemail@gmail.com").isEmpty()) {
            UserModel u1 = new UserModel();
            u1.setUsername("bidder1");
            u1.setCompanyName("companyOne");
            u1.setEmail("bidderemail@gmail.com");
            u1.setPassword(passwordEncoder.encode("bidder123$"));
            u1.setRole(bidderRole);
            userRepository.save(u1);
        }

        if (userRepository.findByEmail("bidderemail2@gmail.com").isEmpty()) {
            UserModel u2 = new UserModel();
            u2.setUsername("bidder2");
            u2.setCompanyName("companyTwo");
            u2.setEmail("bidderemail2@gmail.com");
            u2.setPassword(passwordEncoder.encode("bidder789$"));
            u2.setRole(bidderRole);
            userRepository.save(u2);
        }

        if (userRepository.findByEmail("approveremail@gmail.com").isEmpty()) {
            UserModel a = new UserModel();
            a.setUsername("approver");
            a.setCompanyName("defaultCompany");
            a.setEmail("approveremail@gmail.com");
            a.setPassword(passwordEncoder.encode("approver123$"));
            a.setRole(approverRole);
            userRepository.save(a);
        }
    }
}
