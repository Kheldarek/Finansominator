package com.fs.ps.put.finansominator.security.session;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;

import com.fs.ps.put.finansominator.communication.ParameterNames;
import com.fs.ps.put.finansominator.model.User;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;

/**
 * Created by Kheldar on 22-Apr-17.
 */

public class SessionManager {

    MessageDigest messageDigest;
    User userData;
    byte[] sessionKey;
    static final String PREFS_NAME="SESSION_KEY_PREFS";
    static final String SESSION_KEY = "SK";


    public SessionManager(){
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
        }catch(Exception e)
        {
            Log.e("NO ALG",e.getMessage());
        }
    }

    public void generateSessionKey() {
        try {
            messageDigest.update(userData.getUsername().getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            messageDigest.update(userData.getUsername().getBytes());
        }
        messageDigest.update(userData.getPassword());
        sessionKey = messageDigest.digest();
    }

    public byte[] generatePasswordDigest(String password, byte[] salt) {
        byte[] passwordDigest = new byte[0];
        if (salt.length > 0) {
            try {
                messageDigest.update(password.getBytes("UTF-8"));
            } catch (UnsupportedEncodingException e) {
                messageDigest.update(password.getBytes());
            }
            messageDigest.update(salt);
            passwordDigest = messageDigest.digest();
        }
        return passwordDigest;
    }

    public byte[] getSessionKey() {
        return sessionKey;
    }

    public void setUserData(User userData) {
        this.userData = userData;
    }

    public void saveSessionKey(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String sessionKey = Base64.encodeToString(getSessionKey(), Base64.NO_WRAP);
        editor.putString(SESSION_KEY,sessionKey);
        editor.apply();
    }

    public static byte[] loadSessionKey(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, 0);
        String base64SessionKey = sharedPreferences.getString(SESSION_KEY,"");
        return Base64.decode(base64SessionKey,Base64.NO_WRAP);


    }

    public void saveUsername(Context context,String username){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(ParameterNames.USERNAME,username);
        editor.apply();
    }

    public static String loadUsername(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, 0);
        return sharedPreferences.getString(ParameterNames.USERNAME,"");
    }


}
