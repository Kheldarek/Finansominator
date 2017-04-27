package com.fs.ps.put.finansominator.security.crypto;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

/**
 * Created by Kheldar on 13-Dec-16.
 */

public class CryptoUtils {

    public static final String AES_MODE = "AES/CBC/PKCS5Padding";
    public static final String AES = "AES";
    public static final String RSA = "RSA/ECB/PKCS1Padding";
    private static final String CHARSET = "UTF-8";

    public static PublicKey publicKey;

    public static SecretKey generateAESKey() {
        try {
            KeyGenerator keygen = KeyGenerator.getInstance(AES);
            SecretKey aesKey = keygen.generateKey();
            return aesKey;
        } catch (Exception e) {
            return null;
        }
    }


    public static byte[] AesEncrypt(SecretKey key, IvParameterSpec iv, byte[] data) {
        byte[] encrypted = null;
        try {
            Cipher aesCipher = Cipher.getInstance(AES_MODE);
            aesCipher.init(Cipher.ENCRYPT_MODE, key, iv);
            byte[] cipherTextBuff = aesCipher.doFinal(data);
            encrypted = cipherTextBuff;
        } catch (Exception e) {
            Log.e("ENCERR", e.getMessage());
        }

        return encrypted;

    }

    public static byte[] AesDecrypt(SecretKey key, IvParameterSpec iv, byte[] data) {

        byte[] decrypted = null;
        try {
            Cipher aesCipher = Cipher.getInstance(AES_MODE);

            aesCipher.init(Cipher.DECRYPT_MODE, key, iv);
            byte[] decipheredBuff = aesCipher.doFinal(data);
            decrypted = decipheredBuff;
        } catch (Exception e) {
            Log.e("ENCERR", e.getMessage());
        }
        return decrypted;

    }

    public static byte[] RSAEncrypt(byte[] data, PublicKey key) {
        try {

            byte[] cipherText = null;
            final Cipher cipher = Cipher.getInstance(RSA);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            cipher.update(data);
            cipherText = cipher.doFinal();
            return cipherText;
        } catch (Exception e) {
            return null;
        }
    }

    public static byte[] RSADecrypt(byte[] data, PrivateKey key) {

        try {
            byte[] dectyptedText = null;

            final Cipher cipher = Cipher.getInstance(RSA);

            cipher.init(Cipher.DECRYPT_MODE, key);
            cipher.update(data);
            dectyptedText = cipher.doFinal();
            return dectyptedText;
        } catch (Exception e) {
            return null;
        }

    }

    public static String encryptParameter(String string, SecretKey key, IvParameterSpec iv) {
        try {
            Gson gson = new Gson();
            return gson.toJson(AesEncrypt(key, iv, string.getBytes(CHARSET)));
        } catch (Exception e) {
            return null;
        }

    }

    public static String encryptParameter(byte[] byteArray, SecretKey key, IvParameterSpec iv) {
        Gson gson = new Gson();
        return  gson.toJson(AesEncrypt(key, iv, byteArray));

    }

    public static String encryptKey(SecretKey key) {
        Gson gson = new Gson();
        return gson.toJson(RSAEncrypt(key.getEncoded(),publicKey));
    }

    public static String encryptIv(IvParameterSpec iv){
        Gson gson = new Gson();
        return gson.toJson(RSAEncrypt(iv.getIV(),publicKey));

    }

    public static String decryptStringParameter(String string, SecretKey aesKey, IvParameterSpec ivParameter){
        Gson gson = new Gson();
        try {
            return new String(AesDecrypt(aesKey, ivParameter, gson.fromJson(string, byte[].class)), CHARSET);
        }catch (Exception e){
            return "";
        }
    }

    public static byte[] decryptByteArrayParameter(String byteArray, SecretKey aesKey, IvParameterSpec ivParameter){
        Gson gson = new Gson();
        return AesDecrypt(aesKey, ivParameter, gson.fromJson(byteArray, byte[].class));
    }

}
