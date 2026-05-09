// =================== ReservationService.java ===================
package main.java.com.gearrentpro.service;

import main.java.com.gearrentpro.dao.ReservationDAO;
import main.java.com.gearrentpro.dao.EquipmentDAO;
import main.java.com.gearrentpro.entity.Reservation;
import java.sql.SQLException;
import java.util.List;

public class ReservationService {

    private ReservationDAO reservationDAO = new ReservationDAO();
    private EquipmentDAO equipmentDAO = new EquipmentDAO();

    public List<Reservation> getAllReservations() throws SQLException {
        return reservationDAO.findAll();
    }

    public List<Reservation> getReservationsByBranch(String branchId) throws SQLException {
        return reservationDAO.findByBranch(branchId);
    }

    public Reservation getReservationById(String reservationId) throws SQLException {
        return reservationDAO.findById(reservationId);
    }

    public boolean createReservation(Reservation reservation) throws SQLException {
        long days = reservation.getStartDate().until(reservation.getEndDate()).getDays() + 1;
        if (days > 30) throw new IllegalArgumentException("Reservation cannot exceed 30 days.");
        if (reservationDAO.hasOverlap(reservation.getEquipmentId(),
                reservation.getStartDate(), reservation.getEndDate(), null)) {
            throw new IllegalArgumentException("Equipment already reserved or rented for selected dates.");
        }
        boolean saved = reservationDAO.save(reservation);
        if (saved) {
            equipmentDAO.updateStatus(reservation.getEquipmentId(), "Reserved");
        }
        return saved;
    }

    public boolean cancelReservation(String reservationId) throws SQLException {
        Reservation res = reservationDAO.findById(reservationId);
        boolean cancelled = reservationDAO.cancel(reservationId);
        if (cancelled && res != null) {
            equipmentDAO.updateStatus(res.getEquipmentId(), "Available");
        }
        return cancelled;
    }

    public String generateNextId() throws SQLException {
        return reservationDAO.generateNextId();
    }
}
