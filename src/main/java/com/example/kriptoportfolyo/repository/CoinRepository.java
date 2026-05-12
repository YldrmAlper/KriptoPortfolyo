package com.example.kriptoportfolyo.repository;

import com.example.kriptoportfolyo.entity.Coin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Coin veritabanı erişim katmanı.
 * Kullanıcıya özel coin CRUD işlemleri içerir.
 */
@Repository
public interface CoinRepository extends JpaRepository<Coin, Long> {

    /**
     * Belirli bir kullanıcıya ait tüm coinleri listeler.
     *
     * @param userId kullanıcı ID'si
     * @return kullanıcının coinleri
     */
    List<Coin> findByUserId(Long userId);

    /**
     * Belirli bir kullanıcıya ait belirli bir coini bulur.
     * Yetkilendirme kontrolü için kullanılır.
     *
     * @param id     coin ID'si
     * @param userId kullanıcı ID'si
     * @return bulunan coin (Optional)
     */
    Optional<Coin> findByIdAndUserId(Long id, Long userId);
}
