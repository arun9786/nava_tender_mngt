package com.example.tender.repository;

import com.example.tender.model.BiddingModel;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BiddingRepository extends JpaRepository<BiddingModel, Long> {
    List<BiddingModel> findByBidAmountGreaterThan(Double amount);
}
