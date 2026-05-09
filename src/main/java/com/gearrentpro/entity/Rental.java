package main.java.com.gearrentpro.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Rental {
    private String rentalId;
    private String equipmentId;
    private String customerId;
    private String branchId;
    private String reservationId;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate actualReturnDate;
    private BigDecimal rentalAmount;
    private BigDecimal securityDeposit;
    private BigDecimal membershipDiscount;
    private BigDecimal longRentalDiscount;
    private BigDecimal lateFee;
    private BigDecimal damageCharge;
    private String damageDescription;
    private BigDecimal finalAmount;
    private String paymentStatus;
    private String rentalStatus;

    // Extra fields for display
    private String customerName;
    private String equipmentName;
    private String branchName;

    public Rental() {}

    public String getRentalId() { return rentalId; }
    public void setRentalId(String rentalId) { this.rentalId = rentalId; }

    public String getEquipmentId() { return equipmentId; }
    public void setEquipmentId(String equipmentId) { this.equipmentId = equipmentId; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public String getBranchId() { return branchId; }
    public void setBranchId(String branchId) { this.branchId = branchId; }

    public String getReservationId() { return reservationId; }
    public void setReservationId(String reservationId) { this.reservationId = reservationId; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public LocalDate getActualReturnDate() { return actualReturnDate; }
    public void setActualReturnDate(LocalDate actualReturnDate) { this.actualReturnDate = actualReturnDate; }

    public BigDecimal getRentalAmount() { return rentalAmount; }
    public void setRentalAmount(BigDecimal rentalAmount) { this.rentalAmount = rentalAmount; }

    public BigDecimal getSecurityDeposit() { return securityDeposit; }
    public void setSecurityDeposit(BigDecimal securityDeposit) { this.securityDeposit = securityDeposit; }

    public BigDecimal getMembershipDiscount() { return membershipDiscount; }
    public void setMembershipDiscount(BigDecimal membershipDiscount) { this.membershipDiscount = membershipDiscount; }

    public BigDecimal getLongRentalDiscount() { return longRentalDiscount; }
    public void setLongRentalDiscount(BigDecimal longRentalDiscount) { this.longRentalDiscount = longRentalDiscount; }

    public BigDecimal getLateFee() { return lateFee; }
    public void setLateFee(BigDecimal lateFee) { this.lateFee = lateFee; }

    public BigDecimal getDamageCharge() { return damageCharge; }
    public void setDamageCharge(BigDecimal damageCharge) { this.damageCharge = damageCharge; }

    public String getDamageDescription() { return damageDescription; }
    public void setDamageDescription(String damageDescription) { this.damageDescription = damageDescription; }

    public BigDecimal getFinalAmount() { return finalAmount; }
    public void setFinalAmount(BigDecimal finalAmount) { this.finalAmount = finalAmount; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getRentalStatus() { return rentalStatus; }
    public void setRentalStatus(String rentalStatus) { this.rentalStatus = rentalStatus; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getEquipmentName() { return equipmentName; }
    public void setEquipmentName(String equipmentName) { this.equipmentName = equipmentName; }

    public String getBranchName() { return branchName; }
    public void setBranchName(String branchName) { this.branchName = branchName; }
}
