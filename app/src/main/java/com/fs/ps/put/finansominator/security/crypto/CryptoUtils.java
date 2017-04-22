
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.Base64;
import android.util.Log;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

/**
 * Created by Kheldar on 13-Dec-16.
 */

public class CryptoUtils {

    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static void getStoragePermission(Context context, Activity activity)
    {
        int permission = ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,1

            );
        }
    }

    public static SecretKey generateAESKey(){
        try {
            KeyGenerator keygen = KeyGenerator.getInstance("AES");
            SecretKey aesKey = keygen.generateKey();
            return aesKey;
        }catch (Exception e){return null;}
    }



    public static SecretKey deserializeKey() {


        try {
            ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(Environment.getExternalStorageDirectory().getPath() + "/keys/aeaKey.key"));
            return (SecretKey) inputStream.readObject();
        } catch (Exception e) {
            Log.e("KEY", "NO KEY FILE");
            Log.e("ExMsg", e.getMessage());
            return null;
        }
    }
    public static PublicKey deserializePublicKey() {


        try {
            ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(Environment.getExternalStorageDirectory().getPath() + "/keys/pub.key"));
            return (PublicKey) inputStream.readObject();
        } catch (Exception e) {
            Log.e("KEY", "NO KEY FILE");
            Log.e("ExMsg", e.getMessage());
            return null;
        }
    }
    public static PrivateKey deserializePrivateKey() {


        try {
            ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(Environment.getExternalStorageDirectory().getPath() + "/keys/priv.key"));
            return (PrivateKey) inputStream.readObject();
        } catch (Exception e) {
            Log.e("KEY", "NO KEY FILE");
            Log.e("ExMsg", e.getMessage());
            return null;
        }
    }

    public static String AesEncrypt(SecretKey key, String data) {
        String encrypted = "";
        try {
            Cipher aesCipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            aesCipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] clearTextBuff = data.getBytes();
            byte[] cipherTextBuff = aesCipher.doFinal(clearTextBuff);
            encrypted = Base64.encodeToString(cipherTextBuff, Base64.URL_SAFE | Base64.NO_PADDING | Base64.NO_CLOSE | Base64.NO_WRAP);
        } catch (Exception e) {
            Log.e("ENCERR", e.getMessage());
        }

        return encrypted;

    }

    public static String AesDecrypt(SecretKey key, String data) {

        String decrypted = "";
        try {
            Cipher aesCipher = Cipher.getInstance("AES/ECB/PKCS5Padding");

            aesCipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decipheredBuff = aesCipher.doFinal(Base64.decode(data, Base64.URL_SAFE | Base64.NO_PADDING | Base64.NO_CLOSE | Base64.NO_WRAP));
            decrypted = new String(decipheredBuff);
        } catch (Exception e) {
            Log.e("ENCERR", e.getMessage());
        }
        return decrypted;

    }

    public static String RSAEncrypt(String text, PublicKey key) {
        try {

            String cipherText = null;
            final Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            cipher.update(text.getBytes());
            cipherText = Base64.encodeToString(cipher.doFinal(),Base64.URL_SAFE | Base64.NO_PADDING | Base64.NO_CLOSE | Base64.NO_WRAP);

            return cipherText;
        }catch (Exception e){return null;}
    }

    public static String RSADecrypt(String text, PrivateKey key)  {

        try {
            byte[] dectyptedText = null;

            final Cipher cipher = Cipher.getInstance("RSA");

            cipher.init(Cipher.DECRYPT_MODE, key);
            cipher.update(Base64.decode(text, Base64.URL_SAFE | Base64.NO_PADDING | Base64.NO_CLOSE | Base64.NO_WRAP));
            dectyptedText = cipher.doFinal();
            return new String(dectyptedText);
        }catch (Exception e){return null ;}

    }

}
