package com.sangsolutions.e_commerce;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CryptoHandler {


    private static CryptoHandler instance = null;
    private static byte[] KEY;

    private final static byte[] ivx = Globals.AES_IVX.getBytes();

    protected CryptoHandler() {
        KEY = Globals.AES_SECRET_KEY.getBytes(StandardCharsets.UTF_8);
    }

    public static CryptoHandler getInstance() {

        if (instance == null) {
            instance = new CryptoHandler();
        }
        return instance;
    }

    public String encrypt(String message) throws NoSuchAlgorithmException,
            NoSuchPaddingException, IllegalBlockSizeException,
            BadPaddingException, InvalidKeyException,
            InvalidAlgorithmParameterException {

        byte[] srcBuff = message.getBytes(StandardCharsets.UTF_8);

        SecretKeySpec secretKeySpec = new SecretKeySpec(KEY, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(ivx);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivSpec);

        byte[] dstBuff = cipher.doFinal(srcBuff);

        return Base64.encodeToString(dstBuff, Base64.DEFAULT);

    }

    public String decrypt(String encrypted) throws NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException,
            InvalidAlgorithmParameterException, IllegalBlockSizeException,
            BadPaddingException {

        SecretKeySpec secretKeySpec = new SecretKeySpec(KEY, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(ivx);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivSpec);

        byte[] raw = Base64.decode(encrypted,Base64.DEFAULT);

        byte[] originalBytes = cipher.doFinal(raw);

        return new String(originalBytes, StandardCharsets.UTF_8);

    }


    private static class Globals {
        private static final String AES_IVX ="PA7SVb!K&qUB5V*-";
        private static final String AES_SECRET_KEY = "r2eZ6LGgFETeb8WkJ43QN1Hl94nuqidH";
    }



}
