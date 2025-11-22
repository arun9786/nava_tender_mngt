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
    public ResponseEntity<Object> postBidding(@RequestBody BiddingModel biddingModel, Authentication auth) {
        return biddingService.postBidding(biddingModel,auth);
    }

    @GetMapping("/list")
    public ResponseEntity<Object> getBidding(@RequestParam(name="bidAmount", required = true)  double bidAmount, Authentication auth) {
        return biddingService.getBidding(bidAmount,auth);
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<Object> updateBidding( @PathVariable int id, @RequestBody BiddingModel biddingModel, Authentication auth) {
        return biddingService.updateBidding(id,biddingModel, auth);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Object> deleteBidding(@PathVariable int id, Authentication auth) {
        return biddingService.deleteBidding(id,auth);
    }

}
