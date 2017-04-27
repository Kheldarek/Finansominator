package com.fs.ps.put.finansominator.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
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
import com.fs.ps.put.finansominator.listAdapters.CategoriesListAdapter;
import com.fs.ps.put.finansominator.listAdapters.beans.PersonalBudgetBean;
import com.fs.ps.put.finansominator.model.Category;
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

public class BudgetViewActivity extends AppCompatActivity {

    String budgetName;
    ListView categoryList;
    ArrayAdapter listAdapter;
    List<Category> categoryData;
    TextView budgetTitle;
    Context context;
    long budgetId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget_view);
        categoryList = (ListView) findViewById(R.id.budgetCategoryList);
        TextView fabText = (TextView) findViewById(R.id.FabBudgetVievText);
        budgetTitle  = (TextView) findViewById(R.id.nameTxt);
        budgetName = getIntent().getStringExtra(ParameterNames.NAME);
        budgetTitle.setText(budgetName);
        context= this;
        addCategoryListener();
        addLimitListener();
        fabText.setTypeface(FontManager.getTypeface(getApplicationContext(), FontManager.FONTAWESOME));
        budgetId = getIntent().getLongExtra(ParameterNames.BUDGET_ID,-1);
        CategoryGetter cg = new CategoryGetter(this,String.valueOf(budgetId));
        cg.execute();
    }


    public void goToTransactions(View view)
    {
        Gson gson = new Gson();
        Intent intent = new Intent(this,TransactionsActivity.class);
        intent.putExtra(ParameterNames.BUDGET_ID,budgetId);
        intent.putExtra(ParameterNames.CATEGORY_ID,gson.toJson(categoryData));
        startActivity(intent);
    }

    private void addCategory(){
        final Context context = this;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View viewInflated = inflater.inflate(R.layout.fragment_add_personal_budget_dialog, null);
        final TextView nameField = (TextView) viewInflated.findViewById(R.id.personalBudgetNameDialogTxt);
        builder.setTitle("Add new category").
                setView(viewInflated)
                // Add action buttons
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Log.i("CATEGORY_NAME", nameField.getText().toString());
                        CategoryAdder categoryAdder = new CategoryAdder(String.valueOf(budgetId),nameField.getText().toString(), context);
                        categoryAdder.execute();

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        Dialog dialog = builder.create();
        dialog.show();
    }

    private void addCategoryListener(){
        budgetTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addCategory();
            }
        });
    }

    private void addLimitListener(){
        categoryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position, long i) {
                Category item = (Category) adapter.getItemAtPosition(position);
                addLimit(item);

            }
        });
    }

    private void addLimit(final Category category){

        final Context context = this;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View viewInflated = inflater.inflate(R.layout.fragment_add_personal_budget_dialog, null);
        final TextView nameField = (TextView) viewInflated.findViewById(R.id.personalBudgetNameDialogTxt);
        nameField.setHint("Limit");
        nameField.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setTitle("Add limit to category").
                setView(viewInflated)
                // Add action buttons
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Log.i("CATEGORY_LIMIT", nameField.getText().toString());
                        String limit = nameField.getText().toString();
                        try {
                            double checkLimit = Double.parseDouble(limit);
                            CategoryUpdater categoryUpdater = new CategoryUpdater(String.valueOf(category.getId()),String.valueOf(category.getRelatedBudget().getId()),limit, context);
                            categoryUpdater.execute();
                        }
                        catch (Exception e){
                            Toast.makeText(context,"Invalid number!",Toast.LENGTH_SHORT).show();
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




    class CategoryGetter extends AsyncTask<Void, Void, String> {

        private Gson gson;
        public Context context;
        SecretKey aesKey;
        IvParameterSpec iv;
        String username;
        byte[] sessionKey;
        String budgetId;


        public CategoryGetter(Context context, String budgetId) {

            gson = new Gson();
            this.context = context;
            this.budgetId =budgetId;
            aesKey = CryptoUtils.generateAESKey();
            iv = new IvParameterSpec(SecureRandom.getSeed(16));
            this.username = SessionManager.loadUsername(context);
            this.sessionKey = SessionManager.loadSessionKey(context);
        }

        protected void onPreExecute() {
        }

        protected String doInBackground(Void... urls) {
            try {
                return getCategoryJSON();
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
                categoryData = getCategoryList(response);
                listAdapter = new CategoriesListAdapter(context,R.layout.category_row,categoryData);
                categoryList.setAdapter(listAdapter);
            }
        }

        private String getCategoryJSON() throws Exception {

            String response;
            Map<String, Object> parameters = new HashMap<>();
            parameters.put(ParameterNames.SESSION_KEY, CryptoUtils.encryptParameter(sessionKey, aesKey, iv));
            parameters.put(ParameterNames.USERNAME, CryptoUtils.encryptParameter(username, aesKey, iv));
            parameters.put(ParameterNames.BUDGET_ID, CryptoUtils.encryptParameter(budgetId, aesKey, iv));
            parameters.put(ParameterNames.CIPHER_KEY, CryptoUtils.encryptKey(aesKey));
            parameters.put(ParameterNames.IV, CryptoUtils.encryptIv(iv));
            response = ServerCommunicator.sendAndWaitForResponse("http://192.168.0.1:8080/category/get", parameters);
            return CryptoUtils.decryptStringParameter(response, aesKey, iv);

        }

        private List<Category> getCategoryList(String jsonList) {
            Type budgetListFormatter = new TypeToken<ArrayList<Category>>() {
            }.getType();
            if (jsonList.equals("[]"))
                return new ArrayList<>();
            return gson.fromJson(jsonList, budgetListFormatter);
        }

    }
    class CategoryAdder extends AsyncTask<Void, Void, String> {

        private Gson gson;
        public Context context;
        SecretKey aesKey;
        IvParameterSpec iv;
        String username;
        String budgetID;
        byte[] sessionKey;
        String name;


        public CategoryAdder(String budgetID, String name, Context context) {

            gson = new Gson();
            this.context = context;
            aesKey = CryptoUtils.generateAESKey();
            iv = new IvParameterSpec(SecureRandom.getSeed(16));
            this.username = SessionManager.loadUsername(context);
            this.budgetID = budgetID;
            this.sessionKey = SessionManager.loadSessionKey(context);
            this.name = name;
        }

        protected void onPreExecute() {
        }

        protected String doInBackground(Void... urls) {
            try {
                return addCategory();
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
                    Toast.makeText(context, "Category added succesfully", Toast.LENGTH_LONG).show();
                } else
                    Toast.makeText(context, "Category not added", Toast.LENGTH_LONG).show();

                CategoryGetter bg = new CategoryGetter(context, budgetID);
                bg.execute();

            }
        }

        private String addCategory() throws Exception {

            String response;
            Map<String, Object> parameters = new HashMap<>();
            parameters.put(ParameterNames.SESSION_KEY, CryptoUtils.encryptParameter(sessionKey, aesKey, iv));
            parameters.put(ParameterNames.USERNAME, CryptoUtils.encryptParameter(username, aesKey, iv));
            parameters.put(ParameterNames.BUDGET_ID, CryptoUtils.encryptParameter(budgetID, aesKey, iv));
            parameters.put(ParameterNames.NAME, CryptoUtils.encryptParameter(name,aesKey,iv));
            parameters.put(ParameterNames.CIPHER_KEY, CryptoUtils.encryptKey(aesKey));
            parameters.put(ParameterNames.IV, CryptoUtils.encryptIv(iv));
            response = ServerCommunicator.sendAndWaitForResponse("http://192.168.0.1:8080/category/add", parameters);
            return CryptoUtils.decryptStringParameter(response, aesKey, iv);

        }


    }

    class CategoryUpdater extends AsyncTask<Void, Void, String> {

        private Gson gson;
        public Context context;
        SecretKey aesKey;
        IvParameterSpec iv;
        String username;
        String categoryId;
        byte[] sessionKey;
        String limit;
        String budgetId;


        public CategoryUpdater(String categoryID, String budgetId, String limit, Context context) {

            gson = new Gson();
            this.context = context;
            aesKey = CryptoUtils.generateAESKey();
            iv = new IvParameterSpec(SecureRandom.getSeed(16));
            this.username = SessionManager.loadUsername(context);
            this.categoryId = categoryID;
            this.sessionKey = SessionManager.loadSessionKey(context);
            this.limit = limit;
            this.budgetId = budgetId;
        }

        protected void onPreExecute() {
        }

        protected String doInBackground(Void... urls) {
            try {
                return addCategory();
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
                    Toast.makeText(context, "Category updated succesfully", Toast.LENGTH_LONG).show();
                } else
                    Toast.makeText(context, "Category not updated", Toast.LENGTH_LONG).show();

                CategoryGetter bg = new CategoryGetter(context, budgetId);
                bg.execute();

            }
        }

        private String addCategory() throws Exception {

            String response;
            Map<String, Object> parameters = new HashMap<>();
            parameters.put(ParameterNames.SESSION_KEY, CryptoUtils.encryptParameter(sessionKey, aesKey, iv));
            parameters.put(ParameterNames.USERNAME, CryptoUtils.encryptParameter(username, aesKey, iv));
            parameters.put(ParameterNames.CATEGORY_ID, CryptoUtils.encryptParameter(categoryId, aesKey, iv));
            parameters.put(ParameterNames.LIMIT, CryptoUtils.encryptParameter(limit,aesKey,iv));
            parameters.put(ParameterNames.CIPHER_KEY, CryptoUtils.encryptKey(aesKey));
            parameters.put(ParameterNames.IV, CryptoUtils.encryptIv(iv));
            response = ServerCommunicator.sendAndWaitForResponse("http://192.168.0.1:8080/category/update", parameters);
            return CryptoUtils.decryptStringParameter(response, aesKey, iv);

        }


    }
}
