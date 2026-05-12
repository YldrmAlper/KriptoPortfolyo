package com.example.kriptoportfolyo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

/**
 * Borsa DTO sınıfı.
 * Formdan gelen verileri almak ve frontend'e veri göndermek için kullanılır.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeDto {

    private Long id;

    @NotBlank(message = "Borsa adı boş olamaz")
    @Size(min = 2, max = 100, message = "Borsa adı 2-100 karakter arasında olmalıdır")
    private String name;

    // Frontend'den resim yüklemek için
    private MultipartFile logoFile;

    // Veritabanından gelen resmi frontend'de göstermek için (Base64)
    private String logoBase64;
    private String logoContentType;
}
