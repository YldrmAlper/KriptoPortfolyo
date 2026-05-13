package com.example.kriptoportfolyo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CoinDto {

    private Long id;

    @NotBlank(message = "Coin adı boş olamaz")
    @Size(min = 2, max = 100, message = "Coin adı 2-100 karakter arasında olmalıdır")
    private String name;

    @NotBlank(message = "Sembol boş olamaz")
    @Size(min = 2, max = 20, message = "Sembol 2-20 karakter arasında olmalıdır")
    private String symbol;
    private MultipartFile logoFile;
    private String logoBase64;
    private String logoContentType;
}