package com.example.tender.repository;

import com.example.tender.model.BiddingModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BiddingRepository extends JpaRepository<BiddingModel, Integer> {
    List<BiddingModel> findByBidAmountGreaterThan(Double amount);
}
