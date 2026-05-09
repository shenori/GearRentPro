package main.java.com.gearrentpro.entity;

public class Customer {
    private String customerId;
    private String fullName;
    private String nic;
    private String contact;
    private String email;
    private String address;
    private String membershipLevel;

    public Customer() {}

    public Customer(String customerId, String fullName, String nic, String contact,
                    String email, String address, String membershipLevel) {
        this.customerId = customerId;
        this.fullName = fullName;
        this.nic = nic;
        this.contact = contact;
        this.email = email;
        this.address = address;
        this.membershipLevel = membershipLevel;
    }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getNic() { return nic; }
    public void setNic(String nic) { this.nic = nic; }

    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getMembershipLevel() { return membershipLevel; }
    public void setMembershipLevel(String membershipLevel) { this.membershipLevel = membershipLevel; }

    @Override
    public String toString() { return fullName; }
}
