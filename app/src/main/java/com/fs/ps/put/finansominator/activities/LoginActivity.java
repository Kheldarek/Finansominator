package com.fs.ps.put.finansominator.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fs.ps.put.finansominator.R;
import com.fs.ps.put.finansominator.communication.ServerCommunicator;
import com.fs.ps.put.finansominator.model.User;
import com.fs.ps.put.finansominator.security.session.SessionManager;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    private static final String NO_CONNECTION = "No Internet connection!";
    private static final String API_URL_SALT = "http://192.168.0.1:8080/logon/salt";
    private static final String API_URL_VERIFY = "http://192.168.0.1:8080/logon/verify";


    EditText usernameText;
    EditText passwordText;
    Button loginButton;
    TextView signupLink;
    String salt;
    SessionManager sessionManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        signupLink = (TextView) findViewById(R.id.link_signup);
        loginButton = (Button) findViewById(R.id.btn_login);
        passwordText = (EditText) findViewById(R.id.input_password);
        usernameText = (EditText) findViewById(R.id.input_username);
        sessionManager = new SessionManager();


        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.Theme_AppCompat_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        String email = usernameText.getText().toString();
        String password = passwordText.getText().toString();


        SignInTask signInTask = new SignInTask(email, password, this, progressDialog);
        signInTask.execute();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        loginButton.setEnabled(true);
        Intent intent = new Intent(this, MainMenu.class);
        startActivity(intent);
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = usernameText.getText().toString();
        String password = passwordText.getText().toString();

        if (email.isEmpty()) {
            usernameText.setError("enter a valid username");
            valid = false;
        } else {
            usernameText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            passwordText.setError("Has to be between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            passwordText.setError(null);
        }

        return valid;
    }


    class SignInTask extends AsyncTask<Void, Void, String> {

        private String username;
        private String password;
        byte[] sessionKey;
        private Gson gson;
        private byte[] salt;
        ProgressDialog progressDialog;

        public Context context;

        public SignInTask(String param, String param2, Context con, ProgressDialog progressDialog) {
            username = param;
            password = param2;
            context = con;
            gson = new Gson();
            this.progressDialog = progressDialog;

        }

        protected void onPreExecute() {


        }

        protected String doInBackground(Void... urls) {
            try {

                salt = getSalt();
                prepareSessionKey();
                return verifyLogin();


            } catch (Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        protected void onPostExecute(String response) {

            progressDialog.dismiss();
            if (response == null) {
                Toast.makeText(context, NO_CONNECTION, Toast.LENGTH_LONG).show();
            } else {
                Log.i("INFO", response);
                if (response.equals("true")){
                    onLoginSuccess();
                }
                else
                    onLoginFailed();
            }


            }

            private byte[] getSalt () throws Exception {
                String saltText = getSaltRequest();
                byte[] salt = new byte[0];
                if (!saltText.equals("[]")) {
                    salt = gson.fromJson(saltText, byte[].class);
                }
                return salt;
            }

        private String getSaltRequest() throws Exception {
            String salt;
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("username", username);
            salt = ServerCommunicator.sendAndWaitForResponse(API_URL_SALT, parameters);
            return salt;
        }

        private void prepareSessionKey() {
            User user = new User();
            user.setUsername(username);
            user.setPassword(sessionManager.generatePasswordDigest(password, salt));
            sessionManager.setUserData(user);
            sessionManager.generateSessionKey();
            sessionKey = sessionManager.getSessionKey();
        }

        private String verifyLogin() throws Exception {

            String response = "";
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("username", username);
            parameters.put("sessionKey", gson.toJson(sessionKey));
            response = ServerCommunicator.sendAndWaitForResponse(API_URL_VERIFY, parameters);
            

            return response;

        }
    }

}
