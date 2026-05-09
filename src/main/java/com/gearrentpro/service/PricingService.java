// =================== PricingService.java ===================
package main.java.com.gearrentpro.service;

import main.java.com.gearrentpro.dao.CategoryDAO;
import main.java.com.gearrentpro.entity.Category;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;

public class PricingService {

    private CategoryDAO categoryDAO = new CategoryDAO();

    public BigDecimal calculateRentalAmount(String categoryId, BigDecimal dailyBasePrice,
                                             LocalDate startDate, LocalDate endDate) throws SQLException {
        Category category = categoryDAO.findById(categoryId);
        if (category == null) return BigDecimal.ZERO;

        BigDecimal total = BigDecimal.ZERO;
        LocalDate current = startDate;

        while (!current.isAfter(endDate)) {
            BigDecimal dayPrice = dailyBasePrice.multiply(category.getBasePriceFactor());
            if (isWeekend(current)) {
                dayPrice = dayPrice.multiply(category.getWeekendMultiplier());
            }
            total = total.add(dayPrice);
            current = current.plusDays(1);
        }
        return total.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateMembershipDiscount(String membershipLevel, BigDecimal rentalAmount) {
        BigDecimal discountPercent;
        switch (membershipLevel) {
            case "Gold":   discountPercent = new BigDecimal("10.00"); break;
            case "Silver": discountPercent = new BigDecimal("5.00");  break;
            default:       discountPercent = BigDecimal.ZERO;
        }
        return rentalAmount.multiply(discountPercent).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateLongRentalDiscount(long days, BigDecimal rentalAmount) {
        if (days >= 7) {
            return rentalAmount.multiply(new BigDecimal("10.00"))
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal calculateLateFee(String categoryId, LocalDate endDate, LocalDate returnDate) throws SQLException {
        if (returnDate == null || !returnDate.isAfter(endDate)) return BigDecimal.ZERO;
        Category category = categoryDAO.findById(categoryId);
        if (category == null) return BigDecimal.ZERO;
        long daysLate = endDate.until(returnDate).getDays();
        return category.getLateFeePerDay().multiply(new BigDecimal(daysLate));
    }

    private boolean isWeekend(LocalDate date) {
        return date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY;
    }
}