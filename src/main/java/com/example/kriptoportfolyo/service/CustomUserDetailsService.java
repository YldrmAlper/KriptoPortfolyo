package com.example.kriptoportfolyo.service;

import com.example.kriptoportfolyo.entity.User;
import com.example.kriptoportfolyo.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Spring Security için özel kullanıcı detay servisi.
 * Veritabanından kullanıcı bilgilerini yükleyerek
 * Spring Security'nin kimlik doğrulama mekanizmasına entegre eder.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Kullanıcı adına göre kullanıcı detaylarını yükler.
     * Spring Security kimlik doğrulama sürecinde otomatik olarak çağrılır.
     *
     * @param username giriş yapan kullanıcının adı
     * @return Spring Security UserDetails nesnesi
     * @throws UsernameNotFoundException kullanıcı bulunamazsa
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Kullanıcı bulunamadı: " + username));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(user.getRole())
                .build();
    }
}
