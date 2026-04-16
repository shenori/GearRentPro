package com.gearrentpro.service;

import com.gearrentpro.dao.BranchDAO;
import com.gearrentpro.entity.Branch;
import com.gearrentpro.util.ValidationUtil;

import java.sql.SQLException;
import java.util.List;

public class BranchService {

    private BranchDAO branchDAO;

    public BranchService() {
        this.branchDAO = new BranchDAO();
    }

    public String generateNextId() throws SQLException {
        return branchDAO.generateNextId();
    }

    public void saveBranch(Branch branch) throws SQLException, ValidationException {
        // Validate
        validateBranch(branch);

        // Check for duplicate name
        if (branchDAO.existsByName(branch.getName(), null)) {
            throw new ValidationException("Branch with this name already exists");
        }

        // Generate ID if not set
        if (branch.getBranchId() == null || branch.getBranchId().isEmpty()) {
            branch.setBranchId(branchDAO.generateNextId());
        }

        if (!branchDAO.save(branch)) {
            throw new SQLException("Failed to save branch");
        }
    }

    public void updateBranch(Branch branch) throws SQLException, ValidationException {
        // Validate
        validateBranch(branch);

        // Check for duplicate name (excluding current branch)
        if (branchDAO.existsByName(branch.getName(), branch.getBranchId())) {
            throw new ValidationException("Another branch with this name already exists");
        }

        if (!branchDAO.update(branch)) {
            throw new SQLException("Failed to update branch");
        }
    }

    public void deleteBranch(String branchId) throws SQLException, ValidationException {
        // Check if branch has equipment
        if (branchDAO.hasEquipment(branchId)) {
            throw new ValidationException("Cannot delete branch with existing equipment. Please transfer or remove equipment first.");
        }

        if (!branchDAO.deactivate(branchId)) {
            throw new SQLException("Failed to delete branch");
        }
    }

    public Branch getBranchById(String branchId) throws SQLException {
        return branchDAO.findById(branchId);
    }

    public List<Branch> getAllBranches() throws SQLException {
        return branchDAO.findAll();
    }

    public List<Branch> getAllBranchesIncludingInactive() throws SQLException {
        return branchDAO.findAllIncludingInactive();
    }

    private void validateBranch(Branch branch) throws ValidationException {
        if (branch.getName() == null || branch.getName().trim().isEmpty()) {
            throw new ValidationException("Branch name is required");
        }
        if (branch.getName().length() > 100) {
            throw new ValidationException("Branch name must be less than 100 characters");
        }
        if (branch.getAddress() == null || branch.getAddress().trim().isEmpty()) {
            throw new ValidationException("Address is required");
        }
        if (branch.getContact() == null || branch.getContact().trim().isEmpty()) {
            throw new ValidationException("Contact number is required");
        }
        if (!ValidationUtil.isValidPhone(branch.getContact())) {
            throw new ValidationException("Invalid contact number format");
        }
    }

    // Custom exception for validation errors
    public static class ValidationException extends Exception {
        public ValidationException(String message) {
            super(message);
        }
    }
}