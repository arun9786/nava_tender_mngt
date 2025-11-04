package com.example.tender.dto;

public class BiddingRequest {
    private Integer biddingId;
    private Double bidAmount;
    private Double yearsToComplete;
    public Integer getBiddingId() { return biddingId; }
    public void setBiddingId(Integer biddingId) { this.biddingId = biddingId; }
    public Double getBidAmount() { return bidAmount; }
    public void setBidAmount(Double bidAmount) { this.bidAmount = bidAmount; }
    public Double getYearsToComplete() { return yearsToComplete; }
    public void setYearsToComplete(Double yearsToComplete) { this.yearsToComplete = yearsToComplete; }
}
