package com.curator.pos;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordUtil {

    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int SALT_LENGTH = 12;
    private static final int ITERATIONS = 390000;

    public static String makeDjangoPassword(String password) {
        try {
            // Generate salt
            SecureRandom random = new SecureRandom();
            StringBuilder saltBuilder = new StringBuilder(SALT_LENGTH);
            for (int i = 0; i < SALT_LENGTH; i++) {
                saltBuilder.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
            }
            String salt = saltBuilder.toString();

            // Hash
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), ITERATIONS, 256);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] hash = skf.generateSecret(spec).getEncoded();
            
            String hashBase64 = Base64.getEncoder().encodeToString(hash);
            
            // Format: pbkdf2_sha256$iterations$salt$hash
            return "pbkdf2_sha256$" + ITERATIONS + "$" + salt + "$" + hashBase64;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static boolean verifyDjangoPassword(String password, String djangoHash) {
        try {
            // Django format: pbkdf2_sha256$iterations$salt$hash
            String[] parts = djangoHash.split("\\$");
            if (parts.length != 4) return false;

            int iterations = Integer.parseInt(parts[1]);
            String salt = parts[2];
            String expectedHashBase64 = parts[3];

            // PBKDF2 with HMAC-SHA256
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), iterations, 256);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] hash = skf.generateSecret(spec).getEncoded();
            
            String actualHashBase64 = Base64.getEncoder().encodeToString(hash);
            return expectedHashBase64.equals(actualHashBase64);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
