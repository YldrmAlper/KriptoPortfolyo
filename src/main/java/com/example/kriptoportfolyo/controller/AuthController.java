package com.example.kriptoportfolyo.controller;

import com.example.kriptoportfolyo.dto.UserRegisterDto;
import com.example.kriptoportfolyo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.AnonymousAuthenticationToken;

/**
 * Kimlik doğrulama controller sınıfı.
 * Login ve Register sayfalarının gösterimi ile kayıt form işlemlerini yönetir.
 * Spring Security form-based authentication ile entegre çalışır.
 */
@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Giriş sayfasını gösterir.
     * Spring Security'nin loginPage("/login") ayarıyla eşleşir.
     * URL'de ?error parametresi varsa hata mesajı, ?logout varsa çıkış mesajı gösterilir.
     *
     * @return login.html template
     */
    private boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken);
    }

    @GetMapping("/login")
    public String loginPage() {
        if (isAuthenticated()) {
            return "redirect:/dashboard";
        }
        return "auth/login";
    }

    /**
     * Kayıt sayfasını gösterir.
     * Boş bir UserRegisterDto nesnesini model'e ekleyerek form binding sağlar.
     *
     * @param model Thymeleaf model
     * @return register.html template
     */
    @GetMapping("/register")
    public String registerPage(Model model) {
        if (isAuthenticated()) {
            return "redirect:/dashboard";
        }
        model.addAttribute("userRegisterDto", new UserRegisterDto());
        return "auth/register";
    }

    /**
     * Kayıt formunu işler.
     * - Jakarta Validation ile form verilerini doğrular
     * - Doğrulama hatası varsa formu tekrar gösterir (hata mesajlarıyla)
     * - Başarılı kayıtta /login'e yönlendirir (başarı mesajıyla)
     * - Service katmanından gelen hatayı ekranda gösterir
     *
     * @param dto             form verileri (validated)
     * @param result          doğrulama sonuçları
     * @param model           Thymeleaf model
     * @param redirectAttributes yönlendirme sonrası flash mesajlar
     * @return template veya redirect
     */
    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("userRegisterDto") UserRegisterDto dto,
                               BindingResult result,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        // Her zaman ilk önce kullanıcı adı alınmış mı kontrol et
        if (dto.getUsername() != null && !dto.getUsername().isBlank()
                && userService.isUsernameTaken(dto.getUsername())) {
            result.rejectValue("username", "error.username", "Bu kullanıcı adı zaten kullanılıyor");
        }

        if (result.hasErrors()) {
            return "auth/register";
        }

        try {
            userService.registerUser(dto);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Kayıt başarılı! Giriş yapabilirsiniz.");
            return "redirect:/login";
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "auth/register";
        }
    }
}
