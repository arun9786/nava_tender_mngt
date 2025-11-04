package com.example.tender.controller;

import com.example.tender.dto.BiddingRequest;
import com.example.tender.dto.UpdateStatusRequest;
import com.example.tender.model.BiddingModel;
import com.example.tender.model.UserModel;
import com.example.tender.repository.BiddingRepository;
import com.example.tender.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/bidding")
public class BiddingController {

    private final BiddingRepository biddingRepository;
    private final UserRepository userRepository;

    public BiddingController(BiddingRepository biddingRepository, UserRepository userRepository) {
        this.biddingRepository = biddingRepository;
        this.userRepository = userRepository;
    }

    private boolean hasRole(Authentication auth, String role) {
        return auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).anyMatch(r -> r.equals(role));
    }

    private Integer currentUserId(Authentication auth) {
        String email = auth.getName();
        Optional<UserModel> opt = userRepository.findByEmail(email);
        return opt.map(UserModel::getId).orElse(null);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addBidding(@RequestBody BiddingRequest request, Authentication auth) {
        if (!hasRole(auth, "BIDDER")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only bidders allowed");
        }
        if (request.getBiddingId() == null || request.getBidAmount() == null || request.getYearsToComplete() == null) {
            return ResponseEntity.badRequest().body("Invalid input");
        }

        UserModel bidder = userRepository.findByEmail(auth.getName())
                .orElse(null);

        if (bidder == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid bidder");

        BiddingModel b = new BiddingModel();
        b.setBiddingId(request.getBiddingId());
        b.setProjectName("Metro Phase V 2024");
        b.setBidAmount(request.getBidAmount());
        b.setYearsToComplete(request.getYearsToComplete());
        b.setDateOfBidding(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
        b.setStatus("pending");
        b.setBidderId(bidder.getId());

        BiddingModel saved = biddingRepository.save(b);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping("/list")
    public ResponseEntity<?> listByAmount(@RequestParam(name="bidAmount", required = true) Double bidAmount, Authentication auth) {
        if (!hasRole(auth, "BIDDER") && !hasRole(auth, "APPROVER")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only bidders or approvers are allowed");
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

    @PatchMapping("/update/{id}")
    public ResponseEntity<?> updateStatus(@PathVariable("id") Long id, @RequestBody UpdateStatusRequest request, Authentication auth) {
        if (!hasRole(auth, "APPROVER")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only approver allowed");
        }
        if (request.getStatus() == null) return ResponseEntity.badRequest().body("Invalid input");

        Optional<BiddingModel> opt = biddingRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.badRequest().body("not found");
        BiddingModel b = opt.get();
        b.setStatus(request.getStatus());
        biddingRepository.save(b);
        return ResponseEntity.ok(b);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteBidding(@PathVariable("id") Long id, Authentication auth) {
        Optional<BiddingModel> opt = biddingRepository.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.badRequest().body("not found");
        }

        BiddingModel bidding = opt.get();

        boolean isApprover = hasRole(auth, "APPROVER");
        Integer userId = currentUserId(auth);
        boolean isCreator = bidding.getBidderId() != null && bidding.getBidderId().equals(userId);

        if (!isApprover && !isCreator) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("you don't have permission");
        }

        biddingRepository.delete(bidding);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("deleted successfully");
    }

}
