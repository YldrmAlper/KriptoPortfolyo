package com.example.kriptoportfolyo.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Resim işleme servisi.
 * MultipartFile nesnelerini işleyip byte dizisine dönüştürür.
 */
@Service
public class ImageService {

    /**
     * Yüklenen dosyadan byte dizisini alır.
     *
     * @param file yüklenen MultipartFile nesnesi
     * @return dosyanın byte dizisi veya boşsa null
     * @throws RuntimeException dosya okuma hatası durumunda
     */
    public byte[] getBytesFromFile(MultipartFile file) {
        if (file != null && !file.isEmpty()) {
            try {
                return file.getBytes();
            } catch (IOException e) {
                throw new RuntimeException("Dosya okuma hatası: " + e.getMessage());
            }
        }
        return null;
    }
    
    /**
     * Yüklenen dosyanın içerik tipini (MIME type) döndürür.
     * 
     * @param file yüklenen MultipartFile nesnesi
     * @return içerik tipi (örn. "image/png") veya boşsa null
     */
    public String getContentType(MultipartFile file) {
        if (file != null && !file.isEmpty()) {
            return file.getContentType();
        }
        return null;
    }
}
