package com.example.crypto;

import com.example.config.AppConfig;
import java.nio.charset.StandardCharsets;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public final class CryptoEngine {
    private static final byte[] MAIN_KEY = AppConfig.AES_KEY.getBytes(StandardCharsets.UTF_8);
    private static final byte[] MAIN_IV  = AppConfig.AES_IV.getBytes(StandardCharsets.UTF_8);

    private CryptoEngine() {}

    public static byte[] encrypt(byte[] data) throws Exception {
        return encrypt(data, MAIN_KEY, MAIN_IV);
    }

    public static byte[] encrypt(byte[] data, byte[] key, byte[] iv) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
        return cipher.doFinal(data);
    }

    public static byte[] decryptSafe(byte[] data) {
        return decryptSafe(data, MAIN_KEY, MAIN_IV);
    }

    public static byte[] decryptSafe(byte[] data, byte[] key, byte[] iv) {
        if (data == null || data.length == 0) return data;
        if (data.length % 16 != 0) return null;

        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            return cipher.doFinal(data);
        } catch (Throwable ignored) {
            // Decryption failure
        }
        return null;
    }
}
