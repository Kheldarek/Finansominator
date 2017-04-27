package com.fs.ps.put.finansominator.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fs.ps.put.finansominator.R;
import com.fs.ps.put.finansominator.communication.ResponseCodes;
import com.fs.ps.put.finansominator.communication.ServerCommunicator;
import com.fs.ps.put.finansominator.security.crypto.CryptoUtils;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";
    private static final String NO_CONNECTION = "No Internet connection!";
    private static final String API_URL = "http://192.168.0.1:8080/register/add";

    EditText nameText;
    EditText emailText;
    EditText passwordText;
    EditText reEnterPasswordText;
    Button signupButton;
    TextView loginLink;
    boolean signupSuccessful;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        nameText = (EditText) findViewById(R.id.input_name);
        emailText = (EditText) findViewById(R.id.input_email);
        passwordText = (EditText) findViewById(R.id.input_password);
        reEnterPasswordText = (EditText) findViewById(R.id.input_reEnterPassword);
        signupButton = (Button) findViewById(R.id.btn_signup);
        loginLink = (TextView) findViewById(R.id.link_login);


        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.Theme_AppCompat_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        String name = nameText.getText().toString();
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();
        String reEnterPassword = reEnterPasswordText.getText().toString();

        SignupTask signupTask = new SignupTask(name, email, password, this, progressDialog);
        signupTask.execute();

    }


    public void onSignupSuccess() {
        signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }


    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = nameText.getText().toString();
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();
        String reEnterPassword = reEnterPasswordText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            nameText.setError("at least 3 characters");
            valid = false;
        } else {
            nameText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailText.setError("enter a valid email address");
            valid = false;
        } else {
            emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            passwordText.setError(null);
        }

        if (reEnterPassword.isEmpty() || reEnterPassword.length() < 4 || reEnterPassword.length() > 10 || !(reEnterPassword.equals(password))) {
            reEnterPasswordText.setError("Password Do not match");
            valid = false;
        } else {
            reEnterPasswordText.setError(null);
        }

        return valid;
    }

    class SignupTask extends AsyncTask<Void, Void, String> {

        MessageDigest messageDigest;
        String username;
        String email;
        String password;
        byte[] salt;
        Gson gson;
        ProgressDialog progressDialog;
        SecretKey aesKey;
        IvParameterSpec iv;

        public Context context;

        public SignupTask(String username, String email, String password, Context con, ProgressDialog progressDialog) {
            this.username = username;
            this.email = email;
            this.password = password;
            gson = new Gson();
            context = con;
            this.progressDialog = progressDialog;
            aesKey = CryptoUtils.generateAESKey();
            iv = new IvParameterSpec(SecureRandom.getSeed(16));


            try {
                messageDigest = MessageDigest.getInstance("SHA-256");

            } catch (Exception e) {
                Log.e("NO ALGORITHM", e.getMessage());
            }

        }

        protected void onPreExecute() {
            byte[] result = new byte[32];
            SecureRandom secureRandom = new SecureRandom();
            secureRandom.nextBytes(result);
            salt = result;

        }

        protected String doInBackground(Void... urls) {
            try {
                byte[] digestedPassword = generateDigest(password, salt);
                Map<String, Object> parameters = new HashMap<>();
                parameters.put("username", CryptoUtils.encryptParameter(username,aesKey,iv));
                parameters.put("email", CryptoUtils.encryptParameter(email,aesKey,iv));
                parameters.put("password", CryptoUtils.encryptParameter(gson.toJson(digestedPassword),aesKey,iv));
                parameters.put("salt", CryptoUtils.encryptParameter(gson.toJson(salt),aesKey,iv));
                parameters.put("cipherKey", CryptoUtils.encryptKey(aesKey));
                parameters.put("iv", CryptoUtils.encryptIv(iv));

                String response = ServerCommunicator.sendAndWaitForResponse(API_URL, parameters);
                response = CryptoUtils.decryptStringParameter(response,aesKey,iv);;
                return response;

            } catch (Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        protected void onPostExecute(String response) {

            if (response == null) {
                Toast.makeText(context, NO_CONNECTION, Toast.LENGTH_LONG).show();
                signupSuccessful = false;
            }else

            if (response.equals(ResponseCodes.EMAIL_ALREADY_IN_USE.toString())) {
                Toast.makeText(context, "Email is already in use", Toast.LENGTH_LONG).show();
                signupSuccessful = false;
            }else
            if (response.equals(ResponseCodes.LOGIN_ALREADY_IN_USE.toString())) {
                Toast.makeText(context, "Email is already in use", Toast.LENGTH_LONG).show();
                signupSuccessful = false;
            }else
            if (response.equals(ResponseCodes.REGISTERED.toString())) {
                Toast.makeText(context, "Registration succesfull", Toast.LENGTH_LONG).show();

                signupSuccessful = true;
            }
            if (signupSuccessful) {
                onSignupSuccess();
            } else
                onSignupSuccess();
            progressDialog.dismiss();


            //Log.i("INFO", response);


        }

        private byte[] generateDigest(String password, byte[] salt) throws UnsupportedEncodingException {
            messageDigest.update(password.getBytes("UTF-8"));
            messageDigest.update(salt);
            return messageDigest.digest();
        }

    }


}