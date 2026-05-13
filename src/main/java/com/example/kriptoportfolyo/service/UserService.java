package com.example.kriptoportfolyo.service;

import com.example.kriptoportfolyo.dto.UserRegisterDto;
import com.example.kriptoportfolyo.entity.User;
import com.example.kriptoportfolyo.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * @param dto kayıt formu verileri
     * @throws RuntimeException doğrulama hatası durumunda
     */
    @Transactional
    public void registerUser(UserRegisterDto dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("Bu kullanıcı adı zaten kullanılıyor");
        }

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Bu e-posta adresi zaten kullanılıyor");
        }

        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new RuntimeException("Şifreler eşleşmiyor");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole("ROLE_USER");

        userRepository.save(user);
    }

    /**
     * @param username kontrol edilecek kullanıcı adı
     * @return true ise kullanıcı adı zaten mevcut
     */
    public boolean isUsernameTaken(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * @param username aranacak kullanıcı adı
     * @return bulunan kullanıcı entity'si
     * @throws RuntimeException kullanıcı bulunamazsa
     */
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı: " + username));
    }

    /**
     * @param id kullanıcı ID'si
     * @return bulunan kullanıcı entity'si
     * @throws RuntimeException kullanıcı bulunamazsa
     */
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı: ID " + id));
    }
}
