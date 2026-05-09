// =================== EquipmentService.java ===================
package main.java.com.gearrentpro.service;

import main.java.com.gearrentpro.dao.EquipmentDAO;
import main.java.com.gearrentpro.entity.Equipment;
import java.sql.SQLException;
import java.util.List;

public class EquipmentService {

    private EquipmentDAO equipmentDAO = new EquipmentDAO();

    public List<Equipment> getAllEquipment() throws SQLException {
        return equipmentDAO.findAll();
    }

    public List<Equipment> getEquipmentByBranch(String branchId) throws SQLException {
        return equipmentDAO.findByBranch(branchId);
    }

    public List<Equipment> getAvailableEquipment(String branchId) throws SQLException {
        return equipmentDAO.findAvailableByBranch(branchId);
    }

    public Equipment getEquipmentById(String equipmentId) throws SQLException {
        return equipmentDAO.findById(equipmentId);
    }

    public boolean addEquipment(Equipment equipment) throws SQLException {
        if (equipment.getBrand().isEmpty() || equipment.getModel().isEmpty()) {
            throw new IllegalArgumentException("Brand and Model are required.");
        }
        return equipmentDAO.save(equipment);
    }

    public boolean updateEquipment(Equipment equipment) throws SQLException {
        return equipmentDAO.update(equipment);
    }

    public boolean deleteEquipment(String equipmentId) throws SQLException {
        return equipmentDAO.delete(equipmentId);
    }

    public boolean updateStatus(String equipmentId, String status) throws SQLException {
        return equipmentDAO.updateStatus(equipmentId, status);
    }

    public String generateNextId() throws SQLException {
        return equipmentDAO.generateNextId();
    }
}