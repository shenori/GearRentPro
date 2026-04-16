package com.gearrentpro.util;

import java.util.regex.Pattern;

public class ValidationUtil {

    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    
    private static final Pattern PHONE_PATTERN = 
        Pattern.compile("^[0-9]{3}-?[0-9]{7,10}$");
    
    private static final Pattern NIC_PATTERN = 
        Pattern.compile("^([0-9]{9}[vVxX]|[0-9]{12})$");

    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return true; // Email is optional
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        return PHONE_PATTERN.matcher(phone.trim().replaceAll("\\s", "")).matches();
    }

    public static boolean isValidNIC(String nic) {
        if (nic == null || nic.trim().isEmpty()) {
            return false;
        }
        return NIC_PATTERN.matcher(nic.trim()).matches();
    }

    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    public static boolean isPositiveNumber(Number value) {
        return value != null && value.doubleValue() > 0;
    }

    public static boolean isValidYear(int year) {
        int currentYear = java.time.Year.now().getValue();
        return year >= 1990 && year <= currentYear;
    }
}