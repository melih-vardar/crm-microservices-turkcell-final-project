package com.turkcell.crmmicroserviceshw4.userservice.rules;

import com.turkcell.crmmicroserviceshw4.userservice.model.User;
import com.turkcell.crmmicroserviceshw4.userservice.repository.UserRepository;
import io.github.bothuany.exception.BusinessException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserBusinessRules {

    private final UserRepository userRepository;

    /**
     * Kullanıcı adının benzersiz olup olmadığını kontrol eder.
     * 
     * @param username Kontrol edilecek kullanıcı adı
     * @throws BusinessException Eğer kullanıcı adı zaten varsa
     */
    public void checkIfUsernameExists(String username) {
        if (userRepository.existsByUsername(username)) {
            throw new BusinessException("Username is already taken: " + username);
        }
    }

    /**
     * Kullanıcı adının benzersiz olup olmadığını kontrol eder (güncelleme durumu
     * için).
     * 
     * @param username        Kontrol edilecek kullanıcı adı
     * @param currentUsername Mevcut kullanıcı adı (aynı ise hata fırlatılmaz)
     * @throws BusinessException Eğer kullanıcı adı zaten varsa ve mevcut
     *                           kullanıcının adı değilse
     */
    public void checkIfUsernameExistsForUpdate(String username, String currentUsername) {
        if (!username.equals(currentUsername) && userRepository.existsByUsername(username)) {
            throw new BusinessException("Username is already taken: " + username);
        }
    }

    /**
     * E-posta adresinin benzersiz olup olmadığını kontrol eder.
     * 
     * @param email Kontrol edilecek e-posta adresi
     * @throws BusinessException Eğer e-posta adresi zaten varsa
     */
    public void checkIfEmailExists(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new BusinessException("Email is already in use: " + email);
        }
    }

    /**
     * E-posta adresinin benzersiz olup olmadığını kontrol eder (güncelleme durumu
     * için).
     * 
     * @param email        Kontrol edilecek e-posta adresi
     * @param currentEmail Mevcut e-posta adresi (aynı ise hata fırlatılmaz)
     * @throws BusinessException Eğer e-posta adresi zaten varsa ve mevcut
     *                           kullanıcının e-postası değilse
     */
    public void checkIfEmailExistsForUpdate(String email, String currentEmail) {
        if (!email.equals(currentEmail) && userRepository.existsByEmail(email)) {
            throw new BusinessException("Email is already in use: " + email);
        }
    }

    /**
     * Kullanıcının var olup olmadığını ID'ye göre kontrol eder.
     * 
     * @param id Kontrol edilecek kullanıcı ID'si
     * @throws BusinessException Eğer kullanıcı bulunamazsa
     */
    public void checkIfUserExists(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new BusinessException("User not found with id: " + id);
        }
    }

    /**
     * Kullanıcının e-posta adresine göre var olup olmadığını kontrol eder.
     * 
     * @param email Kontrol edilecek e-posta adresi
     * @throws BusinessException Eğer kullanıcı bulunamazsa
     */
    public void checkIfUserExistsByEmail(String email) {
        if (userRepository.findByEmail(email).isEmpty()) {
            throw new BusinessException("User not found with email: " + email);
        }
    }

    /**
     * Kullanıcının kullanıcı adına göre var olup olmadığını kontrol eder.
     * 
     * @param username Kontrol edilecek kullanıcı adı
     * @throws BusinessException Eğer kullanıcı bulunamazsa
     */
    public void checkIfUserExistsByUsername(String username) {
        if (userRepository.findByUsername(username).isEmpty()) {
            throw new BusinessException("User not found with username: " + username);
        }
    }

    /**
     * Kullanıcının silinip silinmediğini kontrol eder.
     * 
     * @param user Kontrol edilecek kullanıcı
     * @throws BusinessException Eğer kullanıcı silinmişse
     */
    public void checkIfUserIsNotDeleted(User user) {
        if (user.isDeleted()) {
            throw new BusinessException("User is deleted");
        }
    }

    /**
     * Kullanıcının silinmiş olup olmadığını kontrol eder.
     * 
     * @param user Kontrol edilecek kullanıcı
     * @throws BusinessException Eğer kullanıcı silinmemişse
     */
    public void checkIfUserIsDeleted(User user) {
        if (!user.isDeleted()) {
            throw new BusinessException("User is not deleted: " + user.getId());
        }
    }

    /**
     * Token'ın geçerli olup olmadığını kontrol eder.
     * 
     * @param token Kontrol edilecek token
     * @throws BusinessException Eğer token null veya boşsa
     */
    public void checkIfTokenIsValid(String token) {
        if (token == null || token.isEmpty()) {
            throw new BusinessException("Token cannot be null or empty");
        }
    }
}