package com.fs.ps.put.finansominator.security.session;

import android.util.Log;

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


}
