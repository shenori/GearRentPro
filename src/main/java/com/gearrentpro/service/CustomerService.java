// =================== CustomerService.java ===================
package main.java.com.gearrentpro.service;

import main.java.com.gearrentpro.dao.CustomerDAO;
import main.java.com.gearrentpro.entity.Customer;
import java.sql.SQLException;
import java.util.List;

public class CustomerService {

    private CustomerDAO customerDAO = new CustomerDAO();

    public List<Customer> getAllCustomers() throws SQLException {
        return customerDAO.findAll();
    }

    public Customer getCustomerById(String customerId) throws SQLException {
        return customerDAO.findById(customerId);
    }

    public boolean addCustomer(Customer customer) throws SQLException {
        if (customer.getFullName().isEmpty() || customer.getNic().isEmpty()) {
            throw new IllegalArgumentException("Name and NIC are required.");
        }
        return customerDAO.save(customer);
    }

    public boolean updateCustomer(Customer customer) throws SQLException {
        return customerDAO.update(customer);
    }

    public boolean deleteCustomer(String customerId) throws SQLException {
        return customerDAO.delete(customerId);
    }

    public String generateNextId() throws SQLException {
        return customerDAO.generateNextId();
    }
}
