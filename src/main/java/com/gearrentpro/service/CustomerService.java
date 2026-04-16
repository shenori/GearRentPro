package com.gearrentpro.service;

import com.gearrentpro.dao.CustomerDAO;
import com.gearrentpro.entity.Customer;
import com.gearrentpro.entity.Customer.MembershipLevel;
import com.gearrentpro.util.ValidationUtil;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class CustomerService {

    private CustomerDAO customerDAO;

    public CustomerService() {
        this.customerDAO = new CustomerDAO();
    }

    public String generateNextId() throws SQLException {
        return customerDAO.generateNextId();
    }

    public void saveCustomer(Customer customer) throws SQLException, ValidationException {
        validateCustomer(customer);

        // Check for duplicate NIC
        if (customerDAO.existsByNIC(customer.getNicPassport(), null)) {
            throw new ValidationException("Customer with this NIC/Passport already exists");
        }

        if (customer.getCustomerId() == null || customer.getCustomerId().isEmpty()) {
            customer.setCustomerId(customerDAO.generateNextId());
        }

        if (!customerDAO.save(customer)) {
            throw new SQLException("Failed to save customer");
        }
    }

    public void updateCustomer(Customer customer) throws SQLException, ValidationException {
        validateCustomer(customer);

        // Check for duplicate NIC (excluding current customer)
        if (customerDAO.existsByNIC(customer.getNicPassport(), customer.getCustomerId())) {
            throw new ValidationException("Another customer with this NIC/Passport already exists");
        }

        if (!customerDAO.update(customer)) {
            throw new SQLException("Failed to update customer");
        }
    }

    public void deleteCustomer(String customerId) throws SQLException, ValidationException {
        if (customerDAO.hasActiveRentals(customerId)) {
            throw new ValidationException("Cannot delete customer with active rentals");
        }

        if (!customerDAO.deactivate(customerId)) {
            throw new SQLException("Failed to delete customer");
        }
    }

    public Customer getCustomerById(String customerId) throws SQLException {
        return customerDAO.findById(customerId);
    }

    public Customer getCustomerByNIC(String nicPassport) throws SQLException {
        return customerDAO.findByNIC(nicPassport);
    }

    public List<Customer> getAllCustomers() throws SQLException {
        return customerDAO.findAll();
    }

    public List<Customer> getAllCustomersIncludingInactive() throws SQLException {
        return customerDAO.findAllIncludingInactive();
    }

    public List<Customer> searchCustomers(String keyword, MembershipLevel level) throws SQLException {
        return customerDAO.search(keyword, level);
    }

    public BigDecimal getTotalActiveDeposit(String customerId) throws SQLException {
        return customerDAO.getTotalActiveDeposit(customerId);
    }

    private void validateCustomer(Customer customer) throws ValidationException {
        if (customer.getName() == null || customer.getName().trim().isEmpty()) {
            throw new ValidationException("Name is required");
        }
        if (customer.getName().length() > 100) {
            throw new ValidationException("Name must be less than 100 characters");
        }
        if (customer.getNicPassport() == null || customer.getNicPassport().trim().isEmpty()) {
            throw new ValidationException("NIC/Passport is required");
        }
        if (!ValidationUtil.isValidNIC(customer.getNicPassport())) {
            throw new ValidationException("Invalid NIC/Passport format");
        }
        if (customer.getContactNo() == null || customer.getContactNo().trim().isEmpty()) {
            throw new ValidationException("Contact number is required");
        }
        if (!ValidationUtil.isValidPhone(customer.getContactNo())) {
            throw new ValidationException("Invalid contact number format");
        }
        if (customer.getEmail() != null && !customer.getEmail().isEmpty() && 
            !ValidationUtil.isValidEmail(customer.getEmail())) {
            throw new ValidationException("Invalid email format");
        }
    }

    public static class ValidationException extends Exception {
        public ValidationException(String message) {
            super(message);
        }
    }
}