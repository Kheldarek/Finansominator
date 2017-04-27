package com.fs.ps.put.finansominator.activities;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fs.ps.put.finansominator.R;
import com.fs.ps.put.finansominator.communication.ParameterNames;
import com.fs.ps.put.finansominator.communication.ServerCommunicator;
import com.fs.ps.put.finansominator.model.User;
import com.fs.ps.put.finansominator.security.crypto.CryptoUtils;
import com.fs.ps.put.finansominator.security.session.SessionManager;
import com.google.gson.Gson;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class UserActivity extends AppCompatActivity {

    EditText newLogin;
    EditText newMail;
    EditText oldPassword;
    EditText newPassword;
    TextView userLabel;
    TextView emailLabel;
    User user;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        newLogin = (EditText) findViewById(R.id.userNewLoginTxt);
        newMail = (EditText) findViewById(R.id.userEmailEditText);
        oldPassword = (EditText) findViewById(R.id.userOldPasswordEditText);
        newPassword = (EditText) findViewById(R.id.userNewPasswordEditText);
        userLabel = (TextView) findViewById(R.id.userLoginLabel);
        emailLabel = (TextView) findViewById(R.id.userEmailLabel);
        button = (Button) findViewById(R.id.userSendBtn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modify();

            }
        });
        UserGetter userGetter = new UserGetter(this);
        userGetter.execute();
    }



    private void modify(){
        SessionManager sessionManager = new SessionManager();
        sessionManager.setUserData(user);

        if(!newLogin.getText().toString().isEmpty()){
            user.setUsername(newLogin.getText().toString());
        }
        if(!newMail.getText().toString().isEmpty()){
            user.setEmail(newMail.getText().toString());
        }
        if(!newPassword.getText().toString().isEmpty()){
            user.setPassword(sessionManager.generatePasswordDigest(newPassword.getText().toString(),user.getSalt()));

        }

        UserModifier userModifier = new UserModifier(this, user);
        userModifier.execute();
    }


    class UserGetter extends AsyncTask<Void, Void, String> {

        private Gson gson;
        public Context context;
        SecretKey aesKey;
        IvParameterSpec iv;
        String username;
        byte[] sessionKey;


        public UserGetter(Context context) {

            gson = new Gson();
            this.context = context;
            aesKey = CryptoUtils.generateAESKey();
            iv = new IvParameterSpec(SecureRandom.getSeed(16));
            this.username = SessionManager.loadUsername(context);
            this.sessionKey = SessionManager.loadSessionKey(context);
        }

        protected void onPreExecute() {
        }

        protected String doInBackground(Void... urls) {
            try {
                return getUserJson();
            } catch (Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        protected void onPostExecute(String response) {

            if (response == null) {
                Toast.makeText(context, "Cannot connect to the server!", Toast.LENGTH_LONG).show();
            } else {
                Log.i("INFO", response);
                user = getUser(response);
                emailLabel.setText(String.format("Current email: %s", user.getEmail()));
                userLabel.setText(String.format("Current login: %s", user.getUsername()));
            }
        }

        private String getUserJson() throws Exception {

            String response;
            Map<String, Object> parameters = new HashMap<>();
            parameters.put(ParameterNames.SESSION_KEY, CryptoUtils.encryptParameter(sessionKey, aesKey, iv));
            parameters.put(ParameterNames.USERNAME, CryptoUtils.encryptParameter(username, aesKey, iv));
            parameters.put(ParameterNames.CIPHER_KEY, CryptoUtils.encryptKey(aesKey));
            parameters.put(ParameterNames.IV, CryptoUtils.encryptIv(iv));
            response = ServerCommunicator.sendAndWaitForResponse("http://192.168.0.1:8080/user/get", parameters);
            return CryptoUtils.decryptStringParameter(response, aesKey, iv);

        }

        private User getUser(String jsonList) {

            if (jsonList.equals("[]"))
                return null;
            return gson.fromJson(jsonList, User.class);
        }

    }

    class UserModifier extends AsyncTask<Void, Void, String> {

        private Gson gson;
        public Context context;
        SecretKey aesKey;
        IvParameterSpec iv;
        String username;
        byte[] sessionKey;
        User user;


        public UserModifier(Context context, User user) {

            gson = new Gson();
            this.context = context;
            aesKey = CryptoUtils.generateAESKey();
            iv = new IvParameterSpec(SecureRandom.getSeed(16));
            this.username = SessionManager.loadUsername(context);
            this.sessionKey = SessionManager.loadSessionKey(context);
            this.user = user;
        }

        protected void onPreExecute() {
        }

        protected String doInBackground(Void... urls) {
            try {
                return modifyUser();
            } catch (Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        protected void onPostExecute(String response) {

            if (response == null) {
                Toast.makeText(context, "Cannot connect to the server!", Toast.LENGTH_LONG).show();
            } else {
                Log.i("INFO", response);
                if (response.equals("true")) {
                    Toast.makeText(context, "User data changed!", Toast.LENGTH_SHORT).show();
                    emailLabel.setText(String.format("Current email: %s", user.getEmail()));
                    userLabel.setText(String.format("Current login: %s", user.getUsername()));
                } else {
                    Toast.makeText(context, "User data did not change!", Toast.LENGTH_SHORT).show();
                }
            }
        }

        private String modifyUser() throws Exception {

            String response;
            Map<String, Object> parameters = new HashMap<>();
            parameters.put(ParameterNames.SESSION_KEY, CryptoUtils.encryptParameter(sessionKey, aesKey, iv));
            parameters.put(ParameterNames.USERNAME, CryptoUtils.encryptParameter(username, aesKey, iv));
            parameters.put(ParameterNames.MODIFIED_USER, CryptoUtils.encryptParameter(gson.toJson(user), aesKey, iv));
            parameters.put(ParameterNames.CIPHER_KEY, CryptoUtils.encryptKey(aesKey));
            parameters.put(ParameterNames.IV, CryptoUtils.encryptIv(iv));
            response = ServerCommunicator.sendAndWaitForResponse("http://192.168.0.1:8080/user/modify", parameters);
            return CryptoUtils.decryptStringParameter(response, aesKey, iv);

        }


    }
}
