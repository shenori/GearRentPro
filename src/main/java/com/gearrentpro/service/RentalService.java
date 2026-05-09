// =================== RentalService.java ===================
package main.java.com.gearrentpro.service;

import main.java.com.gearrentpro.dao.RentalDAO;
import main.java.com.gearrentpro.dao.EquipmentDAO;
import main.java.com.gearrentpro.entity.Rental;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class RentalService {

    private RentalDAO rentalDAO = new RentalDAO();
    private EquipmentDAO equipmentDAO = new EquipmentDAO();

    public List<Rental> getAllRentals() throws SQLException {
        return rentalDAO.findAll();
    }

    public List<Rental> getRentalsByBranch(String branchId) throws SQLException {
        return rentalDAO.findByBranch(branchId);
    }

    public List<Rental> getOverdueRentals() throws SQLException {
        return rentalDAO.findOverdue();
    }

    public List<Rental> getActiveRentals() throws SQLException {
        return rentalDAO.findByStatus("Active");
    }

    public Rental getRentalById(String rentalId) throws SQLException {
        return rentalDAO.findById(rentalId);
    }

    public boolean createRental(Rental rental) throws SQLException {
        long days = rental.getStartDate().until(rental.getEndDate()).getDays() + 1;
        if (days > 30) throw new IllegalArgumentException("Rental duration cannot exceed 30 days.");
        if (rental.getEndDate().isBefore(rental.getStartDate())) {
            throw new IllegalArgumentException("End date cannot be before start date.");
        }
        boolean saved = rentalDAO.save(rental);
        if (saved) {
            equipmentDAO.updateStatus(rental.getEquipmentId(), "Rented");
        }
        return saved;
    }

    public boolean processReturn(Rental rental) throws SQLException {
        boolean updated = rentalDAO.updateReturn(rental);
        if (updated) {
            String newStatus = (rental.getDamageCharge() != null &&
                    rental.getDamageCharge().doubleValue() > 0) ? "Under Maintenance" : "Available";
            equipmentDAO.updateStatus(rental.getEquipmentId(), newStatus);
        }
        return updated;
    }

    public void updateOverdueRentals() throws SQLException {
        rentalDAO.markOverdue(LocalDate.now());
    }

    public String generateNextId() throws SQLException {
        return rentalDAO.generateNextId();
    }
}