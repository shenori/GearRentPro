package com.gearrentpro.entity;

import java.time.LocalDateTime;

public class Customer {
    private String customerId;
    private String name;
    private String nicPassport;
    private String contactNo;
    private String email;
    private String address;
    private MembershipLevel membershipLevel;
    private boolean isActive;
    private LocalDateTime createdAt;

    public enum MembershipLevel {
        REGULAR("Regular", 0),
        SILVER("Silver", 5),
        GOLD("Gold", 10);

        private final String displayName;
        private final int discountPercentage;

        MembershipLevel(String displayName, int discountPercentage) {
            this.displayName = displayName;
            this.discountPercentage = discountPercentage;
        }

        public String getDisplayName() {
            return displayName;
        }

        public int getDiscountPercentage() {
            return discountPercentage;
        }
    }

    // Constructors
    public Customer() {
        this.membershipLevel = MembershipLevel.REGULAR;
        this.isActive = true;
    }

    public Customer(String customerId, String name, String nicPassport,
                    String contactNo, String email, String address,
                    MembershipLevel membershipLevel) {
        this.customerId = customerId;
        this.name = name;
        this.nicPassport = nicPassport;
        this.contactNo = contactNo;
        this.email = email;
        this.address = address;
        this.membershipLevel = membershipLevel;
        this.isActive = true;
    }

    // Getters and Setters
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getNicPassport() { return nicPassport; }
    public void setNicPassport(String nicPassport) { this.nicPassport = nicPassport; }

    public String getContactNo() { return contactNo; }
    public void setContactNo(String contactNo) { this.contactNo = contactNo; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public MembershipLevel getMembershipLevel() { return membershipLevel; }
    public void setMembershipLevel(MembershipLevel membershipLevel) { this.membershipLevel = membershipLevel; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return name + " (" + customerId + ")";
    }
}