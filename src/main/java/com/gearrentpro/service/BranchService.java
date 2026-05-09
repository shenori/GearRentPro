// =================== BranchService.java ===================
package main.java.com.gearrentpro.service;

import main.java.com.gearrentpro.dao.BranchDAO;
import main.java.com.gearrentpro.entity.Branch;
import java.sql.SQLException;
import java.util.List;

public class BranchService {

    private BranchDAO branchDAO = new BranchDAO();

    public List<Branch> getAllBranches() throws SQLException {
        return branchDAO.findAll();
    }

    public Branch getBranchById(String branchId) throws SQLException {
        return branchDAO.findById(branchId);
    }

    public boolean addBranch(Branch branch) throws SQLException {
        if (branch.getBranchId().isEmpty() || branch.getBranchName().isEmpty()) {
            throw new IllegalArgumentException("Branch ID and Name are required.");
        }
        return branchDAO.save(branch);
    }

    public boolean updateBranch(Branch branch) throws SQLException {
        return branchDAO.update(branch);
    }

    public boolean deleteBranch(String branchId) throws SQLException {
        return branchDAO.delete(branchId);
    }

    public String generateNextId() throws SQLException {
        return branchDAO.generateNextId();
    }
}