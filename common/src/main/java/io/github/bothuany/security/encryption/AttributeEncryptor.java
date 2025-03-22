package io.github.bothuany.security.encryption;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * JPA attribute converter for automatically encrypting and decrypting
 * fields when they are saved to or retrieved from the database.
 */
@Converter
@Component
public class AttributeEncryptor implements AttributeConverter<String, String> {

    private final EncryptionUtil encryptionUtil;

    @Autowired
    public AttributeEncryptor(EncryptionUtil encryptionUtil) {
        this.encryptionUtil = encryptionUtil;
    }

    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (attribute == null) {
            return null;
        }
        return encryptionUtil.encrypt(attribute);
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        return encryptionUtil.decrypt(dbData);
    }
}