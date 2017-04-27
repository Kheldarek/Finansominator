package com.fs.ps.put.finansominator.listAdapters;

import android.app.Activity;
import android.content.Context;
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
import com.fs.ps.put.finansominator.listAdapters.beans.PersonalBudgetBean;
import com.fs.ps.put.finansominator.security.crypto.CryptoUtils;
import com.fs.ps.put.finansominator.security.session.SessionManager;
import com.fs.ps.put.finansominator.utils.FontManager;
import com.google.gson.Gson;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

/**
 * Created by Kheldar on 14-Jan-17.
 */

public class PersonalBudgetAdapter extends ArrayAdapter<PersonalBudgetBean> {

    Context context;
    int resource ;
    List<PersonalBudgetBean> objects;

    public PersonalBudgetAdapter(Context context, int resource, List<PersonalBudgetBean> objects) {
        super(context, resource, objects);

        this.context = context;
        this.resource = resource;
        this.objects =objects;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        RowHolder rowHolder;

        if(row==null){
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(resource,parent,false);

            rowHolder = new RowHolder();
            rowHolder.name = (TextView)row.findViewById(R.id.personalBudgetNameTxt);
            rowHolder.button = (Button)row.findViewById(R.id.delBudgetBtn);
            rowHolder.button.setTypeface(FontManager.getTypeface(context,FontManager.FONTAWESOME));
            rowHolder.button.setFocusable(false);
            rowHolder.button.setTag(R.id.position,position);
            rowHolder.button.setTag(R.id.budget_ID,objects.get(position).id);
            rowHolder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = (int)view.getTag(R.id.position);
                    long budgetID = (long)view.getTag(R.id.budget_ID);
                    BudgetDeleter bd = new BudgetDeleter(String.valueOf(budgetID),context);
                    bd.execute();
                    objects.remove(position);
                    notifyDataSetChanged();


                }
            });
            row.setTag(rowHolder);
        }
        else{
            rowHolder = (RowHolder)row.getTag();
        }

        PersonalBudgetBean object = objects.get(position);
        rowHolder.name.setText(object.name);

        return row;

    }


    static class RowHolder{
        TextView name;
        Button button;
    }

    public PersonalBudgetBean getItem(int position){
        return objects.get(position);
    }

    class BudgetDeleter extends AsyncTask<Void, Void, Void> {

        private Gson gson;
        public Context context;
        SecretKey aesKey;
        IvParameterSpec iv;
        String username;
        String id;
        byte[] sessionKey;


        public BudgetDeleter(String id, Context context) {

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
            parameters.put(ParameterNames.ID, CryptoUtils.encryptParameter(id, aesKey, iv));
            parameters.put(ParameterNames.CIPHER_KEY, CryptoUtils.encryptKey(aesKey));
            parameters.put(ParameterNames.IV, CryptoUtils.encryptIv(iv));
            response = ServerCommunicator.sendAndWaitForResponse("http://192.168.0.1:8080/budget/delete", parameters);
            return response;
        }


    }


}
