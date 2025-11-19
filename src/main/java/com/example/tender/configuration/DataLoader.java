package com.example.tender.configuration;

import com.example.tender.model.RoleModel;
import com.example.tender.model.UserModel;
import com.example.tender.repository.RoleRepository;
import com.example.tender.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements ApplicationRunner {

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        RoleModel bidderRole = roleRepository.save(new RoleModel("BIDDER"));
        RoleModel approverRole = roleRepository.save(new RoleModel("APPROVER"));

        userRepository.save(new UserModel(1, "bidder1", "companyOne", "bidder123$","bidderemail@gmail.com",bidderRole));
        userRepository.save(new UserModel(2, "bidder2", "companyTwo", "bidder789$","bidderemail2@gmail.com",bidderRole));
        userRepository.save(new UserModel(3, "approver", "defaultCompany", "approver123$","approveremail@gmail.com",approverRole));

    }
}
