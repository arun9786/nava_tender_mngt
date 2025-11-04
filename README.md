# Tender Management API

This is a Spring Boot 3.3.4 project (Java 21) that implements the Tender Management API described in the provided problem statement.

## Run
1. Build: `mvn clean package` or `./install.sh`
2. Run: `mvn spring-boot:run`

## Default users
- bidderemail@gmail.com / bidder123$
- bidderemail2@gmail.com / bidder789$
- approveremail@gmail.com / approver123$

## Endpoints
- POST /login
- POST /bidding/add
- GET /bidding/list?bidAmount=...
- PATCH /bidding/update/{id}
- DELETE /bidding/delete/{id}
