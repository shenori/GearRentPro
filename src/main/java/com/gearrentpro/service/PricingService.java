package com.gearrentpro.service;

import com.gearrentpro.dao.CategoryDAO;
import com.gearrentpro.dao.SystemConfigDAO;
import com.gearrentpro.entity.Category;
import com.gearrentpro.entity.Customer;
import com.gearrentpro.entity.Equipment;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.sql.SQLException;

public class PricingService {
    
    private CategoryDAO categoryDAO;
    private SystemConfigDAO configDAO;
    
    public PricingService() {
        this.categoryDAO = new CategoryDAO();
        this.configDAO = new SystemConfigDAO();
    }
    
    public RentalPriceCalculation calculateRentalPrice(
            Equipment equipment, 
            Customer customer,
            LocalDate startDate, 
            LocalDate endDate) throws SQLException {
        
        // Get category for pricing factors
        Category category = categoryDAO.findById(equipment.getCategoryId());
        
        long totalDays = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        
        // Calculate weekend days
        int weekendDays = countWeekendDays(startDate, endDate);
        int weekdays = (int) totalDays - weekendDays;
        
        BigDecimal basePrice = equipment.getDailyBasePrice();
        BigDecimal categoryFactor = category.getBasePriceFactor();
        BigDecimal weekendMultiplier = category.getWeekendMultiplier();
        
        // Calculate weekday charges
        BigDecimal weekdayRate = basePrice.multiply(categoryFactor);
        BigDecimal weekdayTotal = weekdayRate.multiply(BigDecimal.valueOf(weekdays));
        
        // Calculate weekend charges
        BigDecimal weekendRate = basePrice.multiply(categoryFactor).multiply(weekendMultiplier);
        BigDecimal weekendTotal = weekendRate.multiply(BigDecimal.valueOf(weekendDays));
        
        BigDecimal baseRentalAmount = weekdayTotal.add(weekendTotal);
        
        // Long rental discount (if >= 7 days)
        BigDecimal longRentalDiscount = BigDecimal.ZERO;
        int longRentalThreshold = configDAO.getIntValue("LONG_RENTAL_THRESHOLD");
        if (totalDays >= longRentalThreshold) {
            BigDecimal discountPercent = new BigDecimal(configDAO.getValue("LONG_RENTAL_DISCOUNT"));
            longRentalDiscount = baseRentalAmount.multiply(discountPercent).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        }
        
        // Membership discount
        BigDecimal membershipDiscount = BigDecimal.ZERO;
        if (customer.getMembershipLevel() != Customer.MembershipLevel.REGULAR) {
            BigDecimal memberDiscountPercent = getMembershipDiscountPercent(customer.getMembershipLevel());
            membershipDiscount = baseRentalAmount.subtract(longRentalDiscount)
                    .multiply(memberDiscountPercent)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        }
        
        BigDecimal finalAmount = baseRentalAmount
                .subtract(longRentalDiscount)
                .subtract(membershipDiscount);
        
        // Return calculation result
        return new RentalPriceCalculation(
            baseRentalAmount,
            weekendTotal.subtract(weekdayTotal.divide(BigDecimal.valueOf(weekdays > 0 ? weekdays : 1), 2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(weekendDays))),
            longRentalDiscount,
            membershipDiscount,
            finalAmount,
            equipment.getSecurityDeposit()
        );
    }
    
    private int countWeekendDays(LocalDate start, LocalDate end) {
        int weekendDays = 0;
        LocalDate date = start;
        while (!date.isAfter(end)) {
            DayOfWeek day = date.getDayOfWeek();
            if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
                weekendDays++;
            }
            date = date.plusDays(1);
        }
        return weekendDays;
    }
    
    private BigDecimal getMembershipDiscountPercent(Customer.MembershipLevel level) {
        return switch (level) {
            case SILVER -> new BigDecimal("5");
            case GOLD -> new BigDecimal("10");
            default -> BigDecimal.ZERO;
        };
    }
    
    // Inner class to hold calculation results
    public static class RentalPriceCalculation {
        public final BigDecimal baseAmount;
        public final BigDecimal weekendCharges;
        public final BigDecimal longRentalDiscount;
        public final BigDecimal membershipDiscount;
        public final BigDecimal finalAmount;
        public final BigDecimal securityDeposit;
        
        public RentalPriceCalculation(BigDecimal baseAmount, BigDecimal weekendCharges,
                BigDecimal longRentalDiscount, BigDecimal membershipDiscount,
                BigDecimal finalAmount, BigDecimal securityDeposit) {
            this.baseAmount = baseAmount;
            this.weekendCharges = weekendCharges;
            this.longRentalDiscount = longRentalDiscount;
            this.membershipDiscount = membershipDiscount;
            this.finalAmount = finalAmount;
            this.securityDeposit = securityDeposit;
        }
    }
}