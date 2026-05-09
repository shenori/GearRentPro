package main.java.com.gearrentpro.util;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

public class ValidationUtil {

    // ─────────────────────────────────────────────
    //  String / Required field checks
    // ─────────────────────────────────────────────

    /** Returns true if the string is null or blank. */
    public static boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    /** Throws IllegalArgumentException if the field is empty. */
    public static void requireNonEmpty(String value, String fieldName) {
        if (isEmpty(value)) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }
    }

    /** Validates that the string does not exceed a max length. */
    public static void maxLength(String value, int max, String fieldName) {
        if (value != null && value.length() > max) {
            throw new IllegalArgumentException(fieldName + " must not exceed " + max + " characters.");
        }
    }

    // ─────────────────────────────────────────────
    //  Contact / Email / NIC
    // ─────────────────────────────────────────────

    /** Validates a Sri Lankan mobile number (10 digits, starts with 07). */
    public static boolean isValidPhone(String phone) {
        if (isEmpty(phone)) return false;
        return Pattern.matches("^07\\d{8}$", phone.trim());
    }

    /** Validates a basic email address format. */
    public static boolean isValidEmail(String email) {
        if (isEmpty(email)) return false;
        return Pattern.matches("^[\\w._%+\\-]+@[\\w.\\-]+\\.[a-zA-Z]{2,}$", email.trim());
    }

    /**
     * Validates a Sri Lankan NIC.
     * Old format: 9 digits + V/X  (e.g. 123456789V)
     * New format: 12 digits       (e.g. 200012345678)
     */
    public static boolean isValidNIC(String nic) {
        if (isEmpty(nic)) return false;
        String n = nic.trim().toUpperCase();
        return Pattern.matches("^\\d{9}[VX]$", n) || Pattern.matches("^\\d{12}$", n);
    }

    // ─────────────────────────────────────────────
    //  Date checks
    // ─────────────────────────────────────────────

    /** Returns true if the string is a valid LocalDate (yyyy-MM-dd). */
    public static boolean isValidDate(String dateStr) {
        try {
            LocalDate.parse(dateStr);
            return true;
        } catch (DateTimeParseException | NullPointerException e) {
            return false;
        }
    }

    /**
     * Validates a rental / reservation date range.
     * Rules:
     *   - start must not be before today
     *   - end must not be before start
     *   - duration must not exceed 30 days
     */
    public static void validateDateRange(LocalDate start, LocalDate end) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("Start date and end date are required.");
        }
        if (start.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Start date cannot be in the past.");
        }
        if (end.isBefore(start)) {
            throw new IllegalArgumentException("End date cannot be before start date.");
        }
        long days = start.until(end).getDays() + 1;
        if (days > 30) {
            throw new IllegalArgumentException("Rental/reservation duration cannot exceed 30 days.");
        }
    }

    // ─────────────────────────────────────────────
    //  Numeric checks
    // ─────────────────────────────────────────────

    /** Returns true if the string is a valid positive number (integer or decimal). */
    public static boolean isPositiveNumber(String value) {
        try {
            return Double.parseDouble(value) > 0;
        } catch (NumberFormatException | NullPointerException e) {
            return false;
        }
    }

    /** Validates a percentage is between 0 and 100 inclusive. */
    public static void validatePercentage(double value, String fieldName) {
        if (value < 0 || value > 100) {
            throw new IllegalArgumentException(fieldName + " must be between 0 and 100.");
        }
    }

    /** Validates a price factor is greater than 0. */
    public static void validatePriceFactor(double value, String fieldName) {
        if (value <= 0) {
            throw new IllegalArgumentException(fieldName + " must be greater than 0.");
        }
    }

    // ─────────────────────────────────────────────
    //  Security deposit limit
    // ─────────────────────────────────────────────

    private static final double MAX_DEPOSIT_LIMIT = 500_000.0;

    /**
     * Checks whether adding a new deposit would exceed the customer's limit.
     *
     * @param currentTotalDeposit  sum of all active deposits already held
     * @param newDeposit           the deposit for the new rental being created
     */
    public static void validateDepositLimit(double currentTotalDeposit, double newDeposit) {
        if (currentTotalDeposit + newDeposit > MAX_DEPOSIT_LIMIT) {
            throw new IllegalArgumentException(
                String.format("Adding this rental would exceed the customer's deposit limit of LKR %.2f. " +
                              "Current total: LKR %.2f, New deposit: LKR %.2f.",
                              MAX_DEPOSIT_LIMIT, currentTotalDeposit, newDeposit));
        }
    }
}
