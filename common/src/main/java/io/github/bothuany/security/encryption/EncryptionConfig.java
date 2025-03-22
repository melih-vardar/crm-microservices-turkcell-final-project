package io.github.bothuany.security.encryption;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for encryption services.
 * Registers beans for encryption utilities.
 */
@Configuration
public class EncryptionConfig {

    @Value("${encryption.key:1234567812345678}")
    private String key;

    @Value("${encryption.iv:1234567812345678}")
    private String initVector;

    /**
     * Creates and registers the EncryptionUtil bean.
     * 
     * @return An instance of EncryptionUtil
     */
    @Bean
    public EncryptionUtil encryptionUtil() {
        return new EncryptionUtil(key, initVector);
    }

    /**
     * Creates and registers the AttributeEncryptor bean.
     * 
     * @param encryptionUtil The encryption utility to be used by the converter
     * @return An instance of AttributeEncryptor
     */
    @Bean
    public AttributeEncryptor attributeEncryptor(EncryptionUtil encryptionUtil) {
        return new AttributeEncryptor(encryptionUtil);
    }
}