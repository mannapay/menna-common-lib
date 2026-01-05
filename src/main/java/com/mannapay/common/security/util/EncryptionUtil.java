package com.mannapay.common.security.util;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Utility class for AES-256-GCM encryption/decryption.
 * Used for encrypting sensitive data like PII, card numbers, etc.
 */
public class EncryptionUtil {

    private static final Logger log = LoggerFactory.getLogger(EncryptionUtil.class);

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int KEY_SIZE = 256;
    private static final int GCM_TAG_LENGTH = 128;
    private static final int GCM_IV_LENGTH = 12;

    private EncryptionUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Generate a new AES-256 encryption key
     */
    public static String generateKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
        keyGenerator.init(KEY_SIZE);
        SecretKey secretKey = keyGenerator.generateKey();
        return Base64.encodeBase64String(secretKey.getEncoded());
    }

    /**
     * Encrypt plaintext using AES-256-GCM
     *
     * @param plaintext Text to encrypt
     * @param key Base64-encoded encryption key
     * @return Base64-encoded encrypted data with IV prepended
     */
    public static String encrypt(String plaintext, String key) {
        try {
            byte[] keyBytes = Base64.decodeBase64(key);
            SecretKeySpec secretKey = new SecretKeySpec(keyBytes, ALGORITHM);

            // Generate random IV
            byte[] iv = new byte[GCM_IV_LENGTH];
            SecureRandom random = new SecureRandom();
            random.nextBytes(iv);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);

            byte[] encryptedData = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

            // Prepend IV to encrypted data
            ByteBuffer byteBuffer = ByteBuffer.allocate(iv.length + encryptedData.length);
            byteBuffer.put(iv);
            byteBuffer.put(encryptedData);

            return Base64.encodeBase64String(byteBuffer.array());
        } catch (Exception e) {
            log.error("Encryption failed", e);
            throw new RuntimeException("Failed to encrypt data", e);
        }
    }

    /**
     * Decrypt ciphertext using AES-256-GCM
     *
     * @param ciphertext Base64-encoded encrypted data with IV prepended
     * @param key Base64-encoded encryption key
     * @return Decrypted plaintext
     */
    public static String decrypt(String ciphertext, String key) {
        try {
            byte[] keyBytes = Base64.decodeBase64(key);
            SecretKeySpec secretKey = new SecretKeySpec(keyBytes, ALGORITHM);

            byte[] decodedData = Base64.decodeBase64(ciphertext);

            // Extract IV from the beginning
            ByteBuffer byteBuffer = ByteBuffer.wrap(decodedData);
            byte[] iv = new byte[GCM_IV_LENGTH];
            byteBuffer.get(iv);

            // Extract encrypted data
            byte[] encryptedData = new byte[byteBuffer.remaining()];
            byteBuffer.get(encryptedData);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);

            byte[] decryptedData = cipher.doFinal(encryptedData);
            return new String(decryptedData, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("Decryption failed", e);
            throw new RuntimeException("Failed to decrypt data", e);
        }
    }

    /**
     * Mask sensitive data for logging (show only last 4 characters)
     *
     * @param data Data to mask
     * @return Masked data
     */
    public static String maskSensitiveData(String data) {
        if (data == null || data.isEmpty()) {
            return "";
        }
        if (data.length() <= 4) {
            return "****";
        }
        return "*".repeat(data.length() - 4) + data.substring(data.length() - 4);
    }

    /**
     * Mask email address (show first character and domain)
     *
     * @param email Email to mask
     * @return Masked email
     */
    public static String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return "***@***.***";
        }
        String[] parts = email.split("@");
        String localPart = parts[0];
        String maskedLocal = localPart.charAt(0) + "*".repeat(Math.max(0, localPart.length() - 1));
        return maskedLocal + "@" + parts[1];
    }

    /**
     * Mask phone number (show last 4 digits)
     *
     * @param phone Phone number to mask
     * @return Masked phone number
     */
    public static String maskPhoneNumber(String phone) {
        return maskSensitiveData(phone);
    }
}
