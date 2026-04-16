package com.gearrentpro.service;

import com.gearrentpro.dao.EquipmentDAO;
import com.gearrentpro.entity.Equipment;
import com.gearrentpro.entity.Equipment.EquipmentStatus;
import com.gearrentpro.util.ValidationUtil;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class EquipmentService {

    private EquipmentDAO equipmentDAO;

    public EquipmentService() {
        this.equipmentDAO = new EquipmentDAO();
    }

    public String generateNextId() throws SQLException {
        return equipmentDAO.generateNextId();
    }

    public void saveEquipment(Equipment equipment) throws SQLException, ValidationException {
        validateEquipment(equipment);

        if (equipment.getEquipmentId() == null || equipment.getEquipmentId().isEmpty()) {
            equipment.setEquipmentId(equipmentDAO.generateNextId());
        }

        if (!equipmentDAO.save(equipment)) {
            throw new SQLException("Failed to save equipment");
        }
    }

    public void updateEquipment(Equipment equipment) throws SQLException, ValidationException {
        validateEquipment(equipment);

        if (!equipmentDAO.update(equipment)) {
            throw new SQLException("Failed to update equipment");
        }
    }

    public void deleteEquipment(String equipmentId) throws SQLException, ValidationException {
        // Check for active rentals
        if (equipmentDAO.hasActiveRentals(equipmentId)) {
            throw new ValidationException("Cannot delete equipment with active rentals");
        }

        if (!equipmentDAO.delete(equipmentId)) {
            throw new SQLException("Failed to delete equipment");
        }
    }

    public void updateStatus(String equipmentId, EquipmentStatus status) throws SQLException {
        if (!equipmentDAO.updateStatus(equipmentId, status)) {
            throw new SQLException("Failed to update equipment status");
        }
    }

    public Equipment getEquipmentById(String equipmentId) throws SQLException {
        return equipmentDAO.findById(equipmentId);
    }

    public List<Equipment> getAllEquipment() throws SQLException {
        return equipmentDAO.findAll();
    }

    public List<Equipment> getEquipmentByBranch(String branchId) throws SQLException {
        return equipmentDAO.findByBranch(branchId);
    }

    public List<Equipment> searchEquipment(String branchId, String categoryId, 
                                           EquipmentStatus status, String keyword) throws SQLException {
        return equipmentDAO.search(branchId, categoryId, status, keyword);
    }

    public List<Equipment> getAvailableEquipmentByBranch(String branchId) throws SQLException {
        return equipmentDAO.findAvailableByBranch(branchId);
    }

    private void validateEquipment(Equipment equipment) throws ValidationException {
        if (equipment.getCategoryId() == null || equipment.getCategoryId().isEmpty()) {
            throw new ValidationException("Category is required");
        }
        if (equipment.getBranchId() == null || equipment.getBranchId().isEmpty()) {
            throw new ValidationException("Branch is required");
        }
        if (equipment.getBrand() == null || equipment.getBrand().trim().isEmpty()) {
            throw new ValidationException("Brand is required");
        }
        if (equipment.getBrand().length() > 50) {
            throw new ValidationException("Brand must be less than 50 characters");
        }
        if (equipment.getModel() == null || equipment.getModel().trim().isEmpty()) {
            throw new ValidationException("Model is required");
        }
        if (equipment.getModel().length() > 100) {
            throw new ValidationException("Model must be less than 100 characters");
        }
        if (!ValidationUtil.isValidYear(equipment.getPurchaseYear())) {
            throw new ValidationException("Invalid purchase year");
        }
        if (equipment.getDailyBasePrice() == null || 
            equipment.getDailyBasePrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Daily base price must be greater than 0");
        }
        if (equipment.getSecurityDeposit() == null || 
            equipment.getSecurityDeposit().compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Security deposit cannot be negative");
        }
    }

    public static class ValidationException extends Exception {
        public ValidationException(String message) {
            super(message);
        }
    }
}