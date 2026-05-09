package main.java.com.gearrentpro.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordUtil {

    /**
     * Hashes a plain-text password using SHA-256.
     * Use this when saving a new user to the database.
     */
    public static String hash(String plainPassword) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashed = md.digest(plainPassword.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashed) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available.", e);
        }
    }

    /**
     * Verifies a plain-text password against a stored hash.
     * Use this during login.
     */
    public static boolean verify(String plainPassword, String storedHash) {
        return hash(plainPassword).equals(storedHash);
    }

    /**
     * Validates password strength.
     * Rules: min 6 chars, at least one letter and one digit.
     */
    public static boolean isStrongEnough(String password) {
        if (password == null || password.length() < 6) return false;
        boolean hasLetter = false;
        boolean hasDigit  = false;
        for (char c : password.toCharArray()) {
            if (Character.isLetter(c)) hasLetter = true;
            if (Character.isDigit(c))  hasDigit  = true;
        }
        return hasLetter && hasDigit;
    }
}
