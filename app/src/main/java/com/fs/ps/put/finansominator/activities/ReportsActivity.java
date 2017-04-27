package com.fs.ps.put.finansominator.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fs.ps.put.finansominator.R;
import com.fs.ps.put.finansominator.communication.ParameterNames;
import com.fs.ps.put.finansominator.communication.ServerCommunicator;
import com.fs.ps.put.finansominator.formatters.ZlotyFormatter;
import com.fs.ps.put.finansominator.listAdapters.TransactionListAdapter;
import com.fs.ps.put.finansominator.model.Budget;
import com.fs.ps.put.finansominator.model.Category;
import com.fs.ps.put.finansominator.model.Transaction;
import com.fs.ps.put.finansominator.model.TransactionType;
import com.fs.ps.put.finansominator.security.crypto.CryptoUtils;
import com.fs.ps.put.finansominator.security.session.SessionManager;
import com.fs.ps.put.finansominator.utils.FontManager;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class ReportsActivity extends AppCompatActivity {

    public static final String SHOW_ALL = "All Budgets";
    Map<String, Map<String, Float>> dataSet;
    PieChart pieChart;
    List<Transaction> transactions;
    Set<Transaction> transactionSet;
    List<Budget> budgets;
    Set<String> budgetNames;
    Set<String> categoryNames;
    Spinner spinner;
    String currentBudget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);
        TextView fabText = (TextView) findViewById(R.id.editChartFABText);
        fabText.setTypeface(FontManager.getTypeface(getApplicationContext(), FontManager.FONTAWESOME));

        spinner = (Spinner) findViewById(R.id.choseChartModeSpinner);
        final String[] strings = {"Income", "Outcome"};
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, strings);
        spinner.setAdapter(typeAdapter);
        currentBudget = SHOW_ALL;
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                initializeDataSet(adapterView.getItemAtPosition(i).toString());
                buildPieChartDataSet(currentBudget);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        pieChart = (PieChart) findViewById(R.id.pieChart);
        transactions = new ArrayList<>();
        transactionSet = new HashSet<>();
        budgets = new ArrayList<>();
        budgetNames = new HashSet<>();
        categoryNames = new HashSet<>();
        configureChart();
        BudgetsGetter budgetsGetter = new BudgetsGetter(this);
        budgetsGetter.execute();
    }

    private void initializeDataSet(String mode) {
        boolean chartMode;
        chartMode = mode.equals("Income");
        dataSet = new HashMap<>();
        for (Transaction transaction : transactions) {

            String budgetName = transaction.getRelatedBudget().getName();
            String categoryName = transaction.getTransactionCategory().getName();
            Float amount = transaction.getAmount().floatValue();
            boolean isIncome = transaction.getTransactionType().equals(TransactionType.INCOME);

            Map<String, Float> categoryMap = dataSet.get(budgetName);
            if (categoryMap == null) {
                dataSet.put(budgetName, new HashMap<String, Float>());
                if (isIncome == chartMode) {
                    dataSet.get(budgetName).put(categoryName, amount);
                }

            } else {
                Float oldAmount = categoryMap.get(categoryName);
                if (oldAmount == null) {
                    if (isIncome == chartMode) {
                        categoryMap.put(categoryName, amount);
                    }
                } else {
                    if (isIncome == chartMode) {
                        Float newAmount = oldAmount + amount;
                        categoryMap.put(categoryName, newAmount);
                    }
                }
            }


        }
    }

    private void configureChart() {


        pieChart.getLegend().setEnabled(false);
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText("All budgets categories");
        pieChart.animateXY(100, 100);
        //pieChart.setUsePercentValues(true);


    }

    private void buildPieChartDataSet(String budgetName) {
        currentBudget =budgetName;
        List<PieEntry> pieEntries = new ArrayList<>();

        if (budgetName.equals(SHOW_ALL)) {
            for (Map<String, Float> map : dataSet.values()) {
                for (Map.Entry<String, Float> entry : map.entrySet()) {
                    pieEntries.add(new PieEntry(entry.getValue(), entry.getKey()));
                }
            }
        } else {
            Map<String, Float> budgetData = dataSet.get(budgetName);
            for (Map.Entry<String, Float> entry : budgetData.entrySet()) {
                pieEntries.add(new PieEntry(entry.getValue(), entry.getKey()));
            }
        }

        PieDataSet pieDataSet = new PieDataSet(pieEntries, "");
        pieDataSet.setColors(ColorTemplate.JOYFUL_COLORS);

        PieData pieData = new PieData(pieDataSet);
        pieData.setValueTextColor(Color.DKGRAY);
        pieData.setValueTextSize(12);
        pieData.setValueFormatter(new ZlotyFormatter());
        pieChart.setData(pieData);
        pieChart.setCenterText(budgetName);
        pieData.notifyDataChanged();
        pieChart.invalidate();

    }

    public void editChart(View view) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(ReportsActivity.this);
        dialog.setTitle("Choose budget to show!");

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                ReportsActivity.this, android.R.layout.select_dialog_singlechoice);

        adapter.add(SHOW_ALL);
        adapter.addAll(dataSet.keySet());


        dialog.setAdapter(adapter, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                buildPieChartDataSet(adapter.getItem(which));
            }
        });
        dialog.setNegativeButton("Cancel", null);
        dialog.show();
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
                initializeDataSet(spinner.getSelectedItem().toString());
                buildPieChartDataSet(SHOW_ALL);

                if (!getAll) {
                    transactions = getTransactionList(response);
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
}
