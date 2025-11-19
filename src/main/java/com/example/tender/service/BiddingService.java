package com.example.tender.service;

import com.example.tender.model.BiddingModel;
import com.example.tender.model.UserModel;
import com.example.tender.repository.BiddingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class BiddingService {

    @Autowired
    private BiddingRepository biddingRepository;
    @Autowired
    private UserService userService;


    private boolean hasRole(Authentication auth, String role) {
        return auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).anyMatch(r -> r.equals(role));
    }

    public ResponseEntity<Object> postBidding(@RequestBody BiddingModel biddingModel, Authentication auth) {
        if (!hasRole(auth, "BIDDER")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Only bidders allowed");
        }
        if (biddingModel.getBiddingId() == null || biddingModel.getBidAmount() == null || biddingModel.getYearsToComplete() == null) {
            return ResponseEntity.badRequest().body("Invalid input");
        }

        UserModel bidder = userService.getUserByEmail(auth.getName());

        if (bidder == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid bidder");

        BiddingModel b = new BiddingModel();
        b.setBiddingId(biddingModel.getBiddingId());
        b.setProjectName("Metro Phase V 2024");
        b.setBidAmount(biddingModel.getBidAmount());
        b.setYearsToComplete(biddingModel.getYearsToComplete());
        b.setDateOfBidding(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
        b.setStatus("pending");
        b.setBidderId(bidder.getId());
        BiddingModel saved = new BiddingModel();
        try {
            saved = biddingRepository.save(b);
        } catch (Exception ex){
            return ResponseEntity.badRequest().body("Bad Request");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    public ResponseEntity<Object> getBidding(@RequestParam(name="bidAmount", required = true) Double bidAmount, Authentication auth) {
        if (!hasRole(auth, "BIDDER") && !hasRole(auth, "APPROVER")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Only bidders or approvers are allowed");
        } else {
            if (bidAmount != null) {
                List<BiddingModel> list = biddingRepository.findByBidAmountGreaterThan(bidAmount);
                if (list.isEmpty()) {
                    return ResponseEntity.badRequest().body("no data available");
                }
                return ResponseEntity.ok(list);
            } else {
                return ResponseEntity.badRequest().body("no data available");
            }
        }
    }

    public ResponseEntity<Object> updateBidding(int id, BiddingModel biddingModel, Authentication auth) {
        if (!hasRole(auth, "APPROVER")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Only approver allowed");
        }
        if (biddingModel.getStatus() == null) return ResponseEntity.badRequest().body("Invalid input");

        Optional<BiddingModel> opt = biddingRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.badRequest().body("not found");
        BiddingModel b = opt.get();
        b.setStatus(biddingModel.getStatus());
        biddingRepository.save(b);
        return ResponseEntity.ok(b);
    }

    public ResponseEntity<Object> deleteBidding(int id, Authentication auth) {
        Optional<BiddingModel> opt = biddingRepository.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.badRequest().body("not found");
        }

        BiddingModel bidding = opt.get();

        boolean isApprover = hasRole(auth, "APPROVER");

        String email = auth.getName();
        UserModel userModel = userService.getUserByEmail(email);
        Integer userId =userModel.getId();

        boolean isCreator = bidding.getBidderId() != null && bidding.getBidderId().equals(userId);

        if (!isApprover && !isCreator) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("you don't have permission");
        }

        biddingRepository.delete(bidding);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("deleted successfully");
    }



}
