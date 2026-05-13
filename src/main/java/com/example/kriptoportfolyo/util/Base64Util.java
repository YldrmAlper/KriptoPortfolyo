package com.example.kriptoportfolyo.util;

import java.util.Base64;

public class Base64Util {

    private Base64Util() {
    }

    /**
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
