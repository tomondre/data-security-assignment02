package org.example.server.model;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import java.util.Base64;

public class Password {

    private String hashedPassword;
    private String salt;

    public Password(String password) {
        if (password == null) {
            throw new IllegalArgumentException("Null password");
        }
        String message = Password.isLegal(password);
        if (message != null) {
            throw new IllegalArgumentException(message);
        }
        this.salt = generateSalt();
        this.hashedPassword = hashPassword(password, this.salt);
    }

    public static boolean isLegalPassword(String password) {
        return isLegal(password) == null;
    }

    private static String isLegal(String password) {
        if (password == null || password.length() < 6) {
            return "Password must have at least 6 characters";
        }
        int lower = 0;
        int upper = 0;
        int digit = 0;
        int special = 0;
        for (int i = 0; i < password.length(); i++) {
            char ch = password.charAt(i);
            if (Character.isDigit(ch)) {
                digit++;
            } else if (Character.isLowerCase(ch)) {
                lower++;
            } else if (Character.isUpperCase(ch)) {
                upper++;
            } else if (ch == '_' || ch == '-') {
                special++;
            }
        }
        if (lower + upper + digit + special < password.length()) {
            return "Password may only contain letters, digits, hyphens and underscore characters";
        }
        if (lower == 0 || upper == 0 || digit == 0) {
            return "Password must contain at least one uppercase letter, at least one lowercase letter, and at least one digit";
        }

        return null;
    }
    private String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] saltBytes = new byte[16]; // 128 bits
        random.nextBytes(saltBytes);
        return Base64.getEncoder().encodeToString(saltBytes);
    }

    public static String hashPassword(String password, String salt) {
        try {
            int iterations = 65536;
            int keyLength = 256;
            char[] passwordChars = password.toCharArray();
            byte[] saltBytes = Base64.getDecoder().decode(salt);

            KeySpec spec = new PBEKeySpec(passwordChars, saltBytes, iterations, keyLength);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] hashBytes = factory.generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Error while hashing the password", e);
        }
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public String getSalt() {
        return salt;
    }

    @Override
    public String toString() {
        return "Password [hashedPassword=" + hashedPassword + ", salt=" + salt + "]";
    }
}
