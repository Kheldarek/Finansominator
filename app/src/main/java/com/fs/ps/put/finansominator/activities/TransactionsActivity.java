package com.fs.ps.put.finansominator.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fs.ps.put.finansominator.R;
import com.fs.ps.put.finansominator.communication.ParameterNames;
import com.fs.ps.put.finansominator.communication.ServerCommunicator;
import com.fs.ps.put.finansominator.listAdapters.TransactionListAdapter;
import com.fs.ps.put.finansominator.listAdapters.beans.TransactionBean;
import com.fs.ps.put.finansominator.model.Budget;
import com.fs.ps.put.finansominator.model.Category;
import com.fs.ps.put.finansominator.model.Transaction;
import com.fs.ps.put.finansominator.model.TransactionType;
import com.fs.ps.put.finansominator.security.crypto.CryptoUtils;
import com.fs.ps.put.finansominator.security.session.SessionManager;
import com.fs.ps.put.finansominator.utils.FontManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class TransactionsActivity extends AppCompatActivity {

    ListView transactionsListView;
    ArrayAdapter transactionsListAdapter;
    List<Transaction> transactions;
    List<Budget> budgets;
    List<Category> categories;
    long budgetId;
    Set<Transaction> transactionSet;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);
        transactions = new ArrayList<>();
        initTransactionList();

        TextView fabText = (TextView) findViewById(R.id.FABTransactionText);
        Typeface typeface = FontManager.getTypeface(getApplicationContext(), FontManager.FONTAWESOME);
        fabText.setTypeface(typeface);
        transactionSet = new HashSet<>();
        Intent intent = getIntent();
        if (intent.hasExtra(ParameterNames.BUDGET_ID)) {
            budgetId = getIntent().getLongExtra(ParameterNames.BUDGET_ID, -1);
            Type type = new TypeToken<ArrayList<Category>>() {
            }.getType();
            Gson gson = new Gson();
            categories = gson.fromJson(getIntent().getStringExtra(ParameterNames.CATEGORY_ID), type);
            TransactionGetter tg = new TransactionGetter(this, String.valueOf(budgetId), false);
            tg.execute();
        } else {
            fab = (FloatingActionButton) findViewById(R.id.myTransactionFAB);
            fab.setVisibility(View.GONE);
            fabText.setVisibility(View.GONE);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

            BudgetsGetter budgetsGetter = new BudgetsGetter(this);
            budgetsGetter.execute();
        }

    }

    void initTransactionList() {
        transactionsListView = (ListView) findViewById(R.id.transactionListView);
        transactionsListAdapter = new TransactionListAdapter(this, R.layout.transaction_row, transactions, transactionSet);
        transactionsListView.setAdapter(transactionsListAdapter);

    }

    private Category findCorrespondingCategory(String name) {
        for (Category category : categories) {
            if (category.getName().equals(name))
                return category;
        }
        return null;
    }

    private String[] createCategoryNameList() {
        List<String> list = new ArrayList<>();
        for (Category category : categories) {
            list.add(category.getName());
        }
        return list.toArray(new String[0]);

    }


    public void addTransaction(View view) {

        final Context context = this;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View viewInflated = inflater.inflate(R.layout.add_transaction_dialog, null);
        final EditText amount = (EditText) viewInflated.findViewById(R.id.transactionAmountDialogTxt);
        final DatePicker date = (DatePicker) viewInflated.findViewById(R.id.transactionDateDialogTxt);
        final Spinner pickCategory = (Spinner) viewInflated.findViewById(R.id.chooseCategorySpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, createCategoryNameList());
        pickCategory.setAdapter(adapter);
        final Spinner pickType = (Spinner) viewInflated.findViewById(R.id.chooseTypeSpinner);
        String[] strings = {"Income", "Outcome"};
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, strings);
        pickType.setAdapter(typeAdapter);
        builder.setTitle("Add new transaction").
                setView(viewInflated)
                // Add action buttons
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Log.i("TRANSACTION_AMOUNT", amount.getText().toString());
                        int day = date.getDayOfMonth();
                        int month = date.getMonth();
                        int year = date.getYear();

                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, month, day);

                        Date begin = calendar.getTime();
                        String category = (String) pickCategory.getSelectedItem();
                        Category categorya = findCorrespondingCategory(category);
                        int type = pickType.getSelectedItemPosition();
                        Log.i("TRANSACTION_DATE", begin.toString());
                        try {
                            Transaction transaction = new Transaction();
                            transaction.setAmount(new BigDecimal(amount.getText().toString()));
                            transaction.setTransactionCategory(categorya);
                            transaction.setRelatedBudget(categorya.getRelatedBudget());
                            transaction.setTransactionDate(begin);
                            transaction.setTransactionType(type > 0 ? TransactionType.OUTCOME : TransactionType.INCOME);
                            TransactionAdder transactionAdder = new TransactionAdder(String.valueOf(budgetId), transaction, context);
                            transactionAdder.execute();
                            transactionsListAdapter.notifyDataSetChanged();

                        } catch (Exception e) {
                            Toast.makeText(context, "Invalid data", Toast.LENGTH_SHORT).show();
                        }

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        Dialog dialog = builder.create();
        dialog.show();
    }

    class TransactionGetter extends AsyncTask<Void, Void, String> {

        private Gson gson;
        public Context context;
        SecretKey aesKey;
        IvParameterSpec iv;
        String username;
        byte[] sessionKey;
        String budgetId;
        boolean getAll;


        public TransactionGetter(Context context, String budgetId, boolean getAll) {

            gson = new Gson();
            this.context = context;
            this.budgetId = budgetId;
            aesKey = CryptoUtils.generateAESKey();
            iv = new IvParameterSpec(SecureRandom.getSeed(16));
            this.username = SessionManager.loadUsername(context);
            this.sessionKey = SessionManager.loadSessionKey(context);
            this.getAll = getAll;
        }

        protected void onPreExecute() {
        }

        protected String doInBackground(Void... urls) {
            try {
                return getTransactionJSON();
            } catch (Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        protected void onPostExecute(String response) {

            if (response == null) {
                Toast.makeText(context, "Cannot connect to the server!", Toast.LENGTH_LONG).show();
            } else {
                transactions = getTransactionList(response);
                transactionSet.addAll(transactions);
                transactions.clear();
                transactions.addAll(transactionSet);
                transactionsListAdapter = new TransactionListAdapter(context, R.layout.transaction_row, transactions, transactionSet);
                transactionsListView.setAdapter(transactionsListAdapter);

                if (!getAll) {
                    transactions = getTransactionList(response);
                    transactionsListAdapter = new TransactionListAdapter(context, R.layout.transaction_row, transactions, transactionSet);
                    transactionsListView.setAdapter(transactionsListAdapter);
                }


            }
        }

        private String getTransactionJSON() throws Exception {

            String response;
            Map<String, Object> parameters = new HashMap<>();
            parameters.put(ParameterNames.SESSION_KEY, CryptoUtils.encryptParameter(sessionKey, aesKey, iv));
            parameters.put(ParameterNames.USERNAME, CryptoUtils.encryptParameter(username, aesKey, iv));
            parameters.put(ParameterNames.BUDGET_ID, CryptoUtils.encryptParameter(budgetId, aesKey, iv));
            parameters.put(ParameterNames.CIPHER_KEY, CryptoUtils.encryptKey(aesKey));
            parameters.put(ParameterNames.IV, CryptoUtils.encryptIv(iv));
            response = ServerCommunicator.sendAndWaitForResponse("http://192.168.0.1:8080/transaction/getByBudgetAndroid", parameters);
            return CryptoUtils.decryptStringParameter(response, aesKey, iv);

        }

        private List<Transaction> getTransactionList(String jsonList) {
            Type budgetListFormatter = new TypeToken<ArrayList<Transaction>>() {
            }.getType();
            if (jsonList.equals("[]"))
                return new ArrayList<>();
            return gson.fromJson(jsonList, budgetListFormatter);
        }

    }

    class TransactionAdder extends AsyncTask<Void, Void, String> {

        private Gson gson;
        public Context context;
        SecretKey aesKey;
        IvParameterSpec iv;
        String username;
        String budgetID;
        byte[] sessionKey;
        String name;
        Transaction transaction;


        public TransactionAdder(String budgetID, Transaction transaction, Context context) {

            gson = new GsonBuilder().setDateFormat(DateFormat.FULL, DateFormat.FULL).create();
            this.context = context;
            aesKey = CryptoUtils.generateAESKey();
            iv = new IvParameterSpec(SecureRandom.getSeed(16));
            this.username = SessionManager.loadUsername(context);
            this.sessionKey = SessionManager.loadSessionKey(context);
            this.transaction = transaction;
            this.budgetID = budgetID;
        }

        protected void onPreExecute() {
        }

        protected String doInBackground(Void... urls) {
            try {
                return addTransaction();
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
                    Toast.makeText(context, "Transaction added succesfully", Toast.LENGTH_LONG).show();
                } else
                    Toast.makeText(context, "Transaction not added", Toast.LENGTH_LONG).show();

                TransactionGetter bg = new TransactionGetter(context, budgetID, false);
                bg.execute();

            }
        }

        private String addTransaction() throws Exception {

            String response;
            Map<String, Object> parameters = new HashMap<>();
            parameters.put(ParameterNames.SESSION_KEY, CryptoUtils.encryptParameter(sessionKey, aesKey, iv));
            parameters.put(ParameterNames.USERNAME, CryptoUtils.encryptParameter(username, aesKey, iv));
            parameters.put(ParameterNames.TRANSACTION, CryptoUtils.encryptParameter(gson.toJson(transaction), aesKey, iv));
            parameters.put(ParameterNames.CIPHER_KEY, CryptoUtils.encryptKey(aesKey));
            parameters.put(ParameterNames.IV, CryptoUtils.encryptIv(iv));
            response = ServerCommunicator.sendAndWaitForResponse("http://192.168.0.1:8080/transaction/addAndroid", parameters);
            return CryptoUtils.decryptStringParameter(response, aesKey, iv);

        }


    }

    class BudgetsGetter extends AsyncTask<Void, Void, String> {

        private Gson gson;
        public Context context;
        SecretKey aesKey;
        IvParameterSpec iv;
        String username;
        byte[] sessionKey;


        public BudgetsGetter(Context context) {

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
                budgets = getBudgetList(response);
                for (Budget budget : budgets) {
                    TransactionGetter transactionGetter = new TransactionGetter(context, String.valueOf(budget.getId()), true);
                    transactionGetter.execute();
                }


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

}
