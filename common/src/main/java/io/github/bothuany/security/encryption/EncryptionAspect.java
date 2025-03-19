package io.github.bothuany.security.encryption;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Aspect for automatically encrypting and decrypting fields annotated
 * with @Encryptable.
 * This aspect intercepts Spring Data repository methods to encrypt data before
 * saving
 * and decrypt data after loading from the database.
 */
@Aspect
@Component
public class EncryptionAspect {

    private final EncryptionService encryptionService;

    public EncryptionAspect(EncryptionService encryptionService) {
        this.encryptionService = encryptionService;
    }

    /**
     * Pointcut for repository save methods.
     */
    @Pointcut("execution(* org.springframework.data.repository.CrudRepository.save(..))")
    public void saveMethod() {
    }

    /**
     * Pointcut for repository findAll and findById methods.
     */
    @Pointcut("execution(* org.springframework.data.repository.CrudRepository.find*(..))")
    public void findMethods() {
    }

    /**
     * Intercepts save operations to encrypt fields before saving to database.
     *
     * @param joinPoint The AOP join point
     */
    @Before("saveMethod()")
    public void encryptFields(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg != null) {
                processObject(arg, true);
            }
        }
    }

    /**
     * Intercepts find operations to decrypt fields after retrieving from database.
     *
     * @param joinPoint   The AOP join point
     * @param returnValue The return value from the repository method
     */
    @AfterReturning(pointcut = "findMethods()", returning = "returnValue")
    public void decryptFields(JoinPoint joinPoint, Object returnValue) {
        if (returnValue != null) {
            if (returnValue instanceof Collection<?>) {
                ((Collection<?>) returnValue).forEach(item -> processObject(item, false));
            } else {
                processObject(returnValue, false);
            }
        }
    }

    /**
     * Process an object to encrypt or decrypt all fields annotated
     * with @Encryptable.
     *
     * @param object  The object to process
     * @param encrypt True to encrypt, false to decrypt
     */
    private void processObject(Object object, boolean encrypt) {
        if (object == null) {
            return;
        }

        List<Field> fields = getAllFields(object.getClass());

        for (Field field : fields) {
            if (field.isAnnotationPresent(Encryptable.class) && field.getType() == String.class) {
                field.setAccessible(true);
                try {
                    String value = (String) field.get(object);
                    if (value != null && !value.isEmpty()) {
                        String transformedValue = encrypt ? encryptionService.encrypt(value)
                                : encryptionService.decrypt(value);
                        field.set(object, transformedValue);
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Failed to access field for encryption/decryption", e);
                }
            }
        }
    }

    /**
     * Gets all declared fields from a class and its superclasses.
     *
     * @param type The class to get fields from
     * @return List of all fields
     */
    private List<Field> getAllFields(Class<?> type) {
        List<Field> fields = new ArrayList<>();
        Class<?> currentClass = type;

        while (currentClass != null && currentClass != Object.class) {
            fields.addAll(Arrays.asList(currentClass.getDeclaredFields()));
            currentClass = currentClass.getSuperclass();
        }

        return fields;
    }
}