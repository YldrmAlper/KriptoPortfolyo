package com.example.kriptoportfolyo.repository;

import com.example.kriptoportfolyo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Kullanıcı veritabanı erişim katmanı.
 * Spring Security entegrasyonu için username ve email ile arama metodları içerir.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Kullanıcı adına göre kullanıcıyı bulur.
     * Spring Security UserDetailsService tarafından kullanılır.
     *
     * @param username aranacak kullanıcı adı
     * @return bulunan kullanıcı (Optional)
     */
    Optional<User> findByUsername(String username);

    /**
     * E-posta adresine göre kullanıcıyı bulur.
     *
     * @param email aranacak e-posta adresi
     * @return bulunan kullanıcı (Optional)
     */
    Optional<User> findByEmail(String email);

    /**
     * Kullanıcı adının sistemde kayıtlı olup olmadığını kontrol eder.
     *
     * @param username kontrol edilecek kullanıcı adı
     * @return true ise kullanıcı adı zaten mevcut
     */
    boolean existsByUsername(String username);

    /**
     * E-posta adresinin sistemde kayıtlı olup olmadığını kontrol eder.
     *
     * @param email kontrol edilecek e-posta adresi
     * @return true ise e-posta zaten mevcut
     */
    boolean existsByEmail(String email);
}
