package io.github.bothuany.security.encryption;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * Configuration class for encryption functionality.
 * Enables AspectJ auto-proxy for AOP support and registers encryption
 * components.
 */
@Configuration
@EnableAspectJAutoProxy
public class EncryptionConfig {

    @Value("${encryption.secret-key:dGhpcyBpcyBhIHNlY3JldCBrZXkgZm9yIEFFUy0yNTYga2V5IQ==}")
    private String secretKey;

    /**
     * Registers the EncryptionService bean.
     *
     * @return An instance of EncryptionService
     */
    @Bean
    public EncryptionService encryptionService() {
        return new EncryptionService(secretKey);
    }

    /**
     * Registers the EncryptionAspect bean.
     *
     * @param encryptionService The encryption service to be used by the aspect
     * @return An instance of EncryptionAspect
     */
    @Bean
    public EncryptionAspect encryptionAspect(EncryptionService encryptionService) {
        return new EncryptionAspect(encryptionService);
    }
}