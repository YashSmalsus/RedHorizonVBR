package com.smalsus.redhorizonvbr.utils;

import android.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;


public class RSAUtil {

    private static String publicKey = "MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEAya6WJGerV4Mfew5Cz1E+\n" +
            "tzWYtTo0tJ2/ZHxcE3OvJzMVqFBS2P3oNHsEt+FVJIz0d0MnTnax1ZrDJUhOabM8\n" +
            "Ym2u+eszCx4PUda2JUUScrmmz8gQ46XS1PNKLEOH/H6drouMf8OWCxTTcxLBqjVr\n" +
            "ijORLa06HgTKy2R0hRXqScPEG+12dsdFE9B2iJTnrXt7OS4RSOI9LJAvDzCqnVgw\n" +
            "GbiHI50IAIidAZ46uyEd9jVlBHEE/5ST6bfx9xYFZZXUigegJP2MXiW4JHSw5JLD\n" +
            "kEvFTi5JNGbT0tMu3W+n/9Lee6+3cSGHzNzSntAevVnyBbvMi4hXLz6gdvdRbtqv\n" +
            "siVXIZHOe/nEeSPmJvWGxhX6LUwG4QigXUT//Gm7WlC52r6F8R9BC/1cs22S9I2F\n" +
            "UZtfPymP2VAQrwsTAKKatClHaifMDXNcJZchF3zEJzD/pQ+f09zK3+ZrRLol0u+P\n" +
            "wNKYXhnNuuSxlYg72tX++4Y46iwL5jrGKdViagVHL811wvU37v5sUfkaVXyPiPqY\n" +
            "jQsK/GmTmglKEEDoi8SVMd9Uq1hY6c+HdlSwocAV0i4AO1xw0oZABmFgdz3CyhOd\n" +
            "hRpofjkt47hjRUH5B6pW+CySHjhn5x44F/TXy1yRTfBKBqKwSe4iTDKRQrQM4CsU\n" +
            "NAjNh8GZS+LTT3Oe22VFe2MCAwEAAQ==";
    private static String privateKey = "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBAKwTjmvkkZH7PZmr2j9KnpFhYaWoKCZhndb0ONhFP1vi6kpDU98/GJ79CrRuNPJtz86NxTOCZqokEb+YiI3Jz1dUvx4GL1I0UJlgGKUx4Bj7pNlI0l6QUSReAprEB9vPzRvjA56P8tIqkoiEWXS0Km6fNOwoKk5UHk1kAZraUrDDAgMBAAECgYEAgLVwBgvFRqfh/5und0fdK2jnnP0Wav8BDNNH4ZyKM7SbAkkx5v5O8DzdUPBN8jdpBFrOciTLnd+01tbE7SlSSAU4w2Wbn1a59ZQmeiK7Kwx7/28HJUciTv3ZYwjQNJXze5t2t4ubuI6XmoMDOdiCTQNcolRYKcgZtwuWATPQicECQQD7CLtLr6Z/kzxmZacfZLmy+2PECH6gsMret1A7brQiQPKGyxB/ernQcyHsTfs50YVxSXB9ELJ4tYR1kjazHUQLAkEAr3r5DrAxIqyLrFMOZsOT4p8+mwBfqbuEAaRmnk5tGmboVkFaxMUHsOrbqmBtZ7vrN8Vkjz3MkeGScehg8pRBKQJBANYAapKqCflFzj7It8DWgEwpbLJIP5LDRB67DUWtKRjuXxIz+DuEpvUhwTCOhfWTPPwHjELBwjOPIn2MCN+GumMCQQCuL+VV3TOI9bnXnsJm47DCfcxJVD+hXk+UybpLXhIS6R+rr0gIloQh65+NKhavp+u++qPtRCzyifUyOLczVFKxAkAtLVwTIvkfhh3Md2ScOdT/BBAmAdQs3TQek3ZwbkbkmiX+BhZ+PheaWqlMxUiguDzqEna4gDwDZ3WeY7i0y2My";

    public static PublicKey getPublicKey(String base64PublicKey){
        PublicKey publicKey = null;
        try{
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.decode(base64PublicKey.getBytes(),Base64.DEFAULT));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            publicKey = keyFactory.generatePublic(keySpec);
            return publicKey;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return publicKey;
    }

    public static PrivateKey getPrivateKey(String base64PrivateKey){
        PrivateKey privateKey = null;
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.decode(base64PrivateKey.getBytes(),Base64.DEFAULT));
        KeyFactory keyFactory = null;
        try {
            keyFactory = KeyFactory.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            privateKey = keyFactory.generatePrivate(keySpec);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return privateKey;
    }

    public static byte[] encrypt(String data, String publicKey) throws BadPaddingException, IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-1AndMGF1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, getPublicKey(publicKey));
        return cipher.doFinal(data.getBytes());
    }

    public static String decrypt(byte[] data, PrivateKey privateKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-1AndMGF1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return new String(cipher.doFinal(data));
    }

    public static String decrypt(String data, String base64PrivateKey) throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
        return decrypt(Base64.decode(data.getBytes(),Base64.DEFAULT), getPrivateKey(base64PrivateKey));
    }

    public static String getEncryptedData(String data) throws IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, BadPaddingException {
     String encryptedString;
        try {
            encryptedString = Base64.encodeToString(encrypt(data, publicKey),Base64.DEFAULT);
            System.out.println("Encrypte String "+encryptedString);
           // String decryptedString = RSAUtil.decrypt(encryptedString, privateKey);
           // System.out.println(decryptedString);
        } catch (NoSuchAlgorithmException e) {
            encryptedString=null;
            System.err.println(e.getMessage());
        }
        return encryptedString;

    }
}
