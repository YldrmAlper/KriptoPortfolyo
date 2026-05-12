package com.example.kriptoportfolyo.util;

import java.util.Base64;

/**
 * BLOB verilerini Base64 formatına çeviren yardımcı sınıf.
 * Thymeleaf üzerinde resimleri <img src="data:image/png;base64,..."> şeklinde
 * gösterebilmek için kullanılır.
 */
public class Base64Util {

    private Base64Util() {
        // Utility sınıfı olduğu için instance oluşturulmasını engelliyoruz
    }

    /**
     * byte dizisini Base64 string'e dönüştürür.
     *
     * @param bytes dönüştürülecek byte dizisi
     * @return Base64 formatında string, veri yoksa null döner
     */
    public static String encodeToString(byte[] bytes) {
        if (bytes != null && bytes.length > 0) {
            return Base64.getEncoder().encodeToString(bytes);
        }
        return null;
    }
}
