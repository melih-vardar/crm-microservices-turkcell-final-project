# Data Encryption (AES/CBC) with JPA AttributeConverter

This package provides a secure AES/CBC encryption implementation that can be easily integrated with your Spring Boot microservices using JPA's AttributeConverter.

## Features

- Strong AES/CBC encryption with PKCS5 padding
- Automatic encryption/decryption through JPA's AttributeConverter
- Field-level encryption with standard JPA annotations
- No need to modify your business logic for encryption/decryption

## How to Use

### 1. Include the common module in your microservice

Make sure your microservice has a dependency on the common module:

```xml
<dependency>
    <groupId>io.github.bothuany</groupId>
    <artifactId>crm-microservices-common</artifactId>
    <version>${common.version}</version>
</dependency>
```

### 2. Enable the encryption in your Spring Boot application

Import the encryption configuration in your Spring Boot application:

```java
@SpringBootApplication
@Import(io.github.bothuany.security.encryption.EncryptionConfig.class)
public class YourApplication {
    public static void main(String[] args) {
        SpringApplication.run(YourApplication.class, args);
    }
}
```

### 3. Configure the encryption keys

Add the following properties to your `application.properties` or `application.yml`:

```properties
# For application.properties
encryption.key=yourEncryptionKey
encryption.iv=yourInitializationVector

# Or for application.yml
encryption:
  key: yourEncryptionKey
  iv: yourInitializationVector
```

> **Note**: Both the key and IV should be 16 characters (128-bit).
> For security, use different keys for different environments and store them securely.

### 4. Apply the converter to fields that need encryption

Use the standard JPA `@Convert` annotation with the provided AttributeEncryptor:

```java
import io.github.bothuany.security.encryption.AttributeEncryptor;
import jakarta.persistence.Convert;

@Entity
public class Customer {
    @Id
    private Long id;

    private String name;

    @Convert(converter = AttributeEncryptor.class)
    private String personalIdNumber; // This field will be automatically encrypted/decrypted

    @Convert(converter = AttributeEncryptor.class)
    private String creditCardNumber; // This field will be automatically encrypted/decrypted

    // Regular fields, getters, setters, etc.
}
```

### 5. Use your repositories normally

The encryption/decryption happens automatically:

```java
@Service
public class CustomerService {
    private final CustomerRepository customerRepository;

    // Constructor injection...

    public Customer saveCustomer(Customer customer) {
        // Fields are automatically encrypted before saving
        return customerRepository.save(customer);
    }

    public Customer getCustomer(Long id) {
        // Fields are automatically decrypted after retrieving
        return customerRepository.findById(id).orElseThrow();
    }

    public boolean customerExists(String email) {
        // The email parameter will be automatically encrypted for comparison
        return customerRepository.existsByEmail(email);
    }
}
```

## Generating Secure Keys

For security, use random generated keys instead of hardcoded values:

```java
import java.security.SecureRandom;
import java.util.Base64;

public class KeyGenerator {
    public static void main(String[] args) {
        SecureRandom random = new SecureRandom();
        byte[] key = new byte[16]; // 128 bits
        random.nextBytes(key);
        System.out.println(new String(key));
    }
}
```

## Security Considerations

1. Store encryption keys securely, preferably in a vault service like HashiCorp Vault or AWS KMS
2. Use different keys for different environments (dev, test, prod)
3. Consider implementing key rotation mechanisms
4. Ensure that logs and error messages don't expose sensitive data
