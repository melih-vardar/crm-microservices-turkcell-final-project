package io.github.bothuany.security.encryption;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Utility class for encryption and decryption using AES/CBC/PKCS5PADDING
 * algorithm.
 */
@Component
public class EncryptionUtil {

    private final String key;
    private final String initVector;
    private static final String ALGORITHM = "AES/CBC/PKCS5PADDING";

    public EncryptionUtil(
            @Value("${encryption.key:1234567812345678}") String key,
            @Value("${encryption.iv:1234567812345678}") String initVector) {
        this.key = key;
        this.initVector = initVector;
    }

    /**
     * Encrypts the provided plain text.
     *
     * @param value The text to encrypt
     * @return Base64 encoded encrypted text
     */
    public String encrypt(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }

        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes(StandardCharsets.UTF_8));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            byte[] encrypted = cipher.doFinal(value.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception ex) {
            throw new RuntimeException("Error encrypting data", ex);
        }
    }

    /**
     * Decrypts the provided encrypted text.
     *
     * @param encrypted Base64 encoded encrypted text
     * @return Decrypted plain text
     */
    public String decrypt(String encrypted) {
        if (encrypted == null || encrypted.isEmpty()) {
            return encrypted;
        }

        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes(StandardCharsets.UTF_8));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

            byte[] original = cipher.doFinal(Base64.getDecoder().decode(encrypted));
            return new String(original, StandardCharsets.UTF_8);
        } catch (Exception ex) {
            throw new RuntimeException("Error decrypting data", ex);
        }
    }
}