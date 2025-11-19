package com.example.tender.controller;

import com.example.tender.model.BiddingModel;
import com.example.tender.service.BiddingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bidding")
public class BiddingController {

    @Autowired
    private BiddingService biddingService;

    @PostMapping("/add")
    public ResponseEntity<Object> postBidding(BiddingModel biddingModel, Authentication auth) {
        return biddingService.postBidding(biddingModel,auth);
    }

    @GetMapping("/list")
    public ResponseEntity<Object> getBidding( double bidAmount, Authentication auth) {
        return biddingService.getBidding(bidAmount,auth);
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<Object> updateBidding( int id, BiddingModel biddingModel, Authentication auth) {
        return biddingService.updateBidding(id,biddingModel, auth);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Object> deleteBidding(int id, Authentication auth) {
        return biddingService.deleteBidding(id,auth);
    }

}
