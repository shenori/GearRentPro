package main.java.com.gearrentpro.entity;

public class Branch {
    private String branchId;
    private String branchName;
    private String address;
    private String contact;

    public Branch() {}

    public Branch(String branchId, String branchName, String address, String contact) {
        this.branchId = branchId;
        this.branchName = branchName;
        this.address = address;
        this.contact = contact;
    }

    public String getBranchId() { return branchId; }
    public void setBranchId(String branchId) { this.branchId = branchId; }

    public String getBranchName() { return branchName; }
    public void setBranchName(String branchName) { this.branchName = branchName; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }

    @Override
    public String toString() { return branchName; }
}
