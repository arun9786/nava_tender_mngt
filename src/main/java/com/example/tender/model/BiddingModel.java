package com.example.tender.model;


import javax.persistence.*;

@Entity
@Table(name = "bidding")
public class BiddingModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(unique = true)
    private Integer biddingId;
    private String projectName = "Metro Phase V 2024";
    private Double bidAmount;
    private Double yearsToComplete;
    private String dateOfBidding;
    private String status = "pending";

    private Integer bidderId;

    public BiddingModel() {
    }
    // getters and setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getBiddingId() { return biddingId; }
    public void setBiddingId(Integer biddingId) { this.biddingId = biddingId; }

    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }

    public Double getBidAmount() { return bidAmount; }
    public void setBidAmount(Double bidAmount) { this.bidAmount = bidAmount; }

    public Double getYearsToComplete() { return yearsToComplete; }
    public void setYearsToComplete(Double yearsToComplete) { this.yearsToComplete = yearsToComplete; }

    public String getDateOfBidding() { return dateOfBidding; }
    public void setDateOfBidding(String dateOfBidding) { this.dateOfBidding = dateOfBidding; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getBidderId() { return bidderId; }
    public void setBidderId(Integer bidderId) { this.bidderId = bidderId; }
}
