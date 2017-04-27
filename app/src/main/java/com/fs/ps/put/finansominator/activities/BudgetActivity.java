package com.fs.ps.put.finansominator.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fs.ps.put.finansominator.R;
import com.fs.ps.put.finansominator.communication.ParameterNames;
import com.fs.ps.put.finansominator.communication.ServerCommunicator;
import com.fs.ps.put.finansominator.listAdapters.PersonalBudgetAdapter;
import com.fs.ps.put.finansominator.listAdapters.beans.PersonalBudgetBean;
import com.fs.ps.put.finansominator.model.Budget;
import com.fs.ps.put.finansominator.security.crypto.CryptoUtils;
import com.fs.ps.put.finansominator.security.session.SessionManager;
import com.fs.ps.put.finansominator.utils.FontManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class BudgetActivity extends AppCompatActivity {


    ListView personalBudgetList;
    List<PersonalBudgetBean> data;
    ArrayAdapter listAdapter;
    List<Budget> fullData;
    byte[] sessionKey;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_budget);
        initPersonalBudgetsList();
        TextView fabText = (TextView) findViewById(R.id.FABtext);
        Typeface typeface = FontManager.getTypeface(getApplicationContext(), FontManager.FONTAWESOME);
        fabText.setTypeface(typeface);
        sessionKey = SessionManager.loadSessionKey(this);

        BudgetsGetter budgetsGetter = new BudgetsGetter(SessionManager.loadUsername(this), this);
        budgetsGetter.execute();

    }

    void initPersonalBudgetsList() {
        personalBudgetList = (ListView) findViewById(R.id.personalBudgetsList);
        data = new ArrayList<>();
        listAdapter = new PersonalBudgetAdapter(this, R.layout.personal_budget_row, data);
        personalBudgetList.setAdapter(listAdapter);
        personalBudgetList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position, long i) {
                PersonalBudgetBean item = (PersonalBudgetBean) adapter.getItemAtPosition(position);
                Intent intent = new Intent(getApplicationContext(), BudgetViewActivity.class);
                intent.putExtra(ParameterNames.BUDGET_ID,item.id);
                intent.putExtra(ParameterNames.NAME,item.name);
                startActivity(intent);
            }
        });
        ;

    }

    List<PersonalBudgetBean> getPersonalBudgetsData() {
        data = new ArrayList<>();
        for (Budget budget : fullData) {
            data.add(new PersonalBudgetBean(budget.getName(), budget.getId()));
        }
        return data;
    }

    public void addNewBudget(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final Context context = this;
        View viewInflated = inflater.inflate(R.layout.fragment_add_personal_budget_dialog, null);
        final TextView nameField = (TextView) viewInflated.findViewById(R.id.personalBudgetNameDialogTxt);
        builder.setTitle("Add new personal budget").
                setView(viewInflated)
                // Add action buttons
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Log.i("PERSONAL_BUDGET_NAME", nameField.getText().toString());
                        BudgetAdder ba = new BudgetAdder(SessionManager.loadUsername(context), nameField.getText().toString().trim(), context);
                        ba.execute();

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        Dialog dialog = builder.create();
        dialog.show();


    }

    class BudgetsGetter extends AsyncTask<Void, Void, String> {

        private Gson gson;
        public Context context;
        SecretKey aesKey;
        IvParameterSpec iv;
        String username;


        public BudgetsGetter(String username, Context context) {

            gson = new Gson();
            this.context = context;
            aesKey = CryptoUtils.generateAESKey();
            iv = new IvParameterSpec(SecureRandom.getSeed(16));
            this.username = username;
        }

        protected void onPreExecute() {
        }

        protected String doInBackground(Void... urls) {
            try {
                return getBudgetJSON();
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
                fullData = getBudgetList(response);
                data = getPersonalBudgetsData();
                listAdapter = new PersonalBudgetAdapter(context, R.layout.personal_budget_row, data);
                personalBudgetList.setAdapter(listAdapter);


            }
        }

        private String getBudgetJSON() throws Exception {

            String response;
            Map<String, Object> parameters = new HashMap<>();
            parameters.put(ParameterNames.SESSION_KEY, CryptoUtils.encryptParameter(sessionKey, aesKey, iv));
            parameters.put(ParameterNames.USERNAME, CryptoUtils.encryptParameter(username, aesKey, iv));
            parameters.put(ParameterNames.CIPHER_KEY, CryptoUtils.encryptKey(aesKey));
            parameters.put(ParameterNames.IV, CryptoUtils.encryptIv(iv));
            response = ServerCommunicator.sendAndWaitForResponse("http://192.168.0.1:8080/budget/get", parameters);
            return CryptoUtils.decryptStringParameter(response, aesKey, iv);

        }

        private List<Budget> getBudgetList(String jsonList) {
            Type budgetListFormatter = new TypeToken<ArrayList<Budget>>() {
            }.getType();
            if (jsonList.equals("[]"))
                return new ArrayList<>();
            return gson.fromJson(jsonList, budgetListFormatter);
        }

    }

    class BudgetAdder extends AsyncTask<Void, Void, String> {

        private Gson gson;
        public Context context;
        SecretKey aesKey;
        IvParameterSpec iv;
        String username;
        String name;


        public BudgetAdder(String username, String name, Context context) {

            gson = new Gson();
            this.context = context;
            aesKey = CryptoUtils.generateAESKey();
            iv = new IvParameterSpec(SecureRandom.getSeed(16));
            this.username = username;
            this.name = name;
        }

        protected void onPreExecute() {
        }

        protected String doInBackground(Void... urls) {
            try {
                return addBudget();
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
                if (Boolean.getBoolean(response)) {
                    Toast.makeText(context, "Budget added succesfully", Toast.LENGTH_LONG).show();
                } else
                    Toast.makeText(context, "Budget added succesfully", Toast.LENGTH_LONG).show();

                BudgetsGetter bg = new BudgetsGetter(username, context);
                bg.execute();

            }
        }

        private String addBudget() throws Exception {

            String response;
            Map<String, Object> parameters = new HashMap<>();
            parameters.put(ParameterNames.SESSION_KEY, CryptoUtils.encryptParameter(sessionKey, aesKey, iv));
            parameters.put(ParameterNames.USERNAME, CryptoUtils.encryptParameter(username, aesKey, iv));
            parameters.put(ParameterNames.NAME, CryptoUtils.encryptParameter(name, aesKey, iv));
            parameters.put(ParameterNames.CIPHER_KEY, CryptoUtils.encryptKey(aesKey));
            parameters.put(ParameterNames.IV, CryptoUtils.encryptIv(iv));
            response = ServerCommunicator.sendAndWaitForResponse("http://192.168.0.1:8080/budget/add", parameters);
            CryptoUtils.decryptStringParameter(response, aesKey, iv);
            return response;
        }


    }

}
