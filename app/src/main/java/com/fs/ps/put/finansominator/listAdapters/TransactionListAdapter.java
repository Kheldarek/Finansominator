package com.fs.ps.put.finansominator.listAdapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.fs.ps.put.finansominator.R;
import com.fs.ps.put.finansominator.communication.ParameterNames;
import com.fs.ps.put.finansominator.communication.ServerCommunicator;
import com.fs.ps.put.finansominator.listAdapters.beans.TransactionBean;
import com.fs.ps.put.finansominator.model.Transaction;
import com.fs.ps.put.finansominator.model.TransactionType;
import com.fs.ps.put.finansominator.security.crypto.CryptoUtils;
import com.fs.ps.put.finansominator.security.session.SessionManager;
import com.fs.ps.put.finansominator.utils.FontManager;
import com.google.gson.Gson;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

/**
 * Created by Kheldar on 14-Jan-17.
 */

public class TransactionListAdapter extends ArrayAdapter<Transaction> {

    Context context;
    int resource;
    List<Transaction> objects;
    Set<Transaction> transactionSet;

    public TransactionListAdapter(Context context, int resource, List<Transaction> objects, Set<Transaction> transactionSet) {
        super(context, resource, objects);

        this.context = context;
        this.resource = resource;
        this.objects = objects;
        this.transactionSet = transactionSet;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        RowHolder rowHolder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(resource, parent, false);

            rowHolder = new RowHolder();
            rowHolder.budget = (TextView) row.findViewById(R.id.transactionBudgetTxt);
            rowHolder.amount = (TextView) row.findViewById(R.id.transactionAmountTxt);
            rowHolder.date = (TextView) row.findViewById(R.id.transactionDateTxt);
            rowHolder.category = (TextView) row.findViewById(R.id.transactionCategoryTxt);
            rowHolder.button = (Button) row.findViewById(R.id.delTransactionBtn);
            rowHolder.button.setTypeface(FontManager.getTypeface(context,FontManager.FONTAWESOME));
            rowHolder.button.setTag(R.id.position,position);
            rowHolder.button.setTag(R.id.budget_ID,objects.get(position).getId());
            rowHolder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = (int)view.getTag(R.id.position);
                    long budgetID = (long)view.getTag(R.id.budget_ID);
                    TransactionDeleter bd = new TransactionDeleter(String.valueOf(budgetID),context);
                    bd.execute();
                    Transaction object = objects.get(position);
                    transactionSet.remove(object);
                    objects.remove(position);
                    notifyDataSetChanged();


                }
            });

            row.setTag(rowHolder);
        } else {
            rowHolder = (RowHolder) row.getTag();
        }

        Transaction object = objects.get(position);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");

        rowHolder.budget.setText(object.getRelatedBudget().getName());
        if(object.getTransactionType().equals(TransactionType.INCOME)) {
            rowHolder.amount.setText(String.format("Amount: +%s", object.getAmount()));
            rowHolder.amount.setTextColor(Color.GREEN);
        }
        else {
            rowHolder.amount.setText(String.format("Amount: -%s", object.getAmount()));
            rowHolder.amount.setTextColor(Color.RED);
        }

        rowHolder.date.setText(String.format("Date: %s" ,simpleDateFormat.format(object.getTransactionDate())));
        rowHolder.category.setText(String.format("Category: %s", object.getTransactionCategory().getName()));

        return row;

    }


    static class RowHolder {
        TextView budget;
        TextView amount;
        TextView date;
        TextView category;
        Button button;
    }

    class TransactionDeleter extends AsyncTask<Void, Void, Void> {

        private Gson gson;
        public Context context;
        SecretKey aesKey;
        IvParameterSpec iv;
        String username;
        String id;
        byte[] sessionKey;


        public TransactionDeleter(String id, Context context) {

            gson = new Gson();
            this.context = context;
            aesKey = CryptoUtils.generateAESKey();
            iv = new IvParameterSpec(SecureRandom.getSeed(16));
            this.username = SessionManager.loadUsername(context);
            this.id = id;
            this.sessionKey = SessionManager.loadSessionKey(context);

        }

        protected void onPreExecute() {
        }

        protected Void doInBackground(Void... urls) {
            try {
                deleteBudget();
                return null ;
            } catch (Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }


        private String deleteBudget() throws Exception {

            String response;
            Map<String, Object> parameters = new HashMap<>();
            parameters.put(ParameterNames.SESSION_KEY, CryptoUtils.encryptParameter(sessionKey, aesKey, iv));
            parameters.put(ParameterNames.USERNAME, CryptoUtils.encryptParameter(username, aesKey, iv));
            parameters.put(ParameterNames.TRANSACTION_ID, CryptoUtils.encryptParameter(id, aesKey, iv));
            parameters.put(ParameterNames.CIPHER_KEY, CryptoUtils.encryptKey(aesKey));
            parameters.put(ParameterNames.IV, CryptoUtils.encryptIv(iv));
            response = ServerCommunicator.sendAndWaitForResponse("http://192.168.0.1:8080/transaction/delete", parameters);
            return response;
        }


    }
}