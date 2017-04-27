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
import com.fs.ps.put.finansominator.model.Category;
import com.fs.ps.put.finansominator.security.crypto.CryptoUtils;
import com.fs.ps.put.finansominator.security.session.SessionManager;
import com.fs.ps.put.finansominator.utils.FontManager;
import com.google.gson.Gson;

import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

/**
 * Created by Kheldar on 19-Apr-17.
 */

public class CategoriesListAdapter extends ArrayAdapter<Category> {

    Context context;
    int resource ;
    List<Category> objects;

    public CategoriesListAdapter(Context context, int resource, List<Category> objects) {
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
            rowHolder.name = (TextView)row.findViewById(R.id.categoryNameTxt);
            rowHolder.button = (Button)row.findViewById(R.id.delCategoryBtn);
            rowHolder.limit = (TextView)row.findViewById(R.id.categoryLimitTxt);
            rowHolder.button.setTypeface(FontManager.getTypeface(context,FontManager.FONTAWESOME));
            rowHolder.button.setFocusable(false);
            rowHolder.button.setTag(R.id.position,position);
            rowHolder.button.setTag(R.id.budget_ID,objects.get(position).getId());
            rowHolder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = (int)view.getTag(R.id.position);
                    long budgetID = (long)view.getTag(R.id.budget_ID);
                    CategoryDeleter bd = new CategoryDeleter(String.valueOf(budgetID),context);
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

        Category object = objects.get(position);
        rowHolder.name.setText(object.getName());
        if(object.getLimit()!=null)
            rowHolder.limit.setText(new DecimalFormat("0.00").format(object.getLimit()));
        else
            rowHolder.limit.setText(R.string.no_limit);


        return row;

    }


    static class RowHolder{
        TextView name;
        TextView limit;
        Button button;
    }

    public Category getItem(int position){
        return objects.get(position);
    }

    class CategoryDeleter extends AsyncTask<Void, Void, Void> {

        private Gson gson;
        public Context context;
        SecretKey aesKey;
        IvParameterSpec iv;
        String username;
        String id;
        byte[] sessionKey;


        public CategoryDeleter(String id, Context context) {

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
            parameters.put(ParameterNames.CATEGORY_ID, CryptoUtils.encryptParameter(id, aesKey, iv));
            parameters.put(ParameterNames.CIPHER_KEY, CryptoUtils.encryptKey(aesKey));
            parameters.put(ParameterNames.IV, CryptoUtils.encryptIv(iv));
            response = ServerCommunicator.sendAndWaitForResponse("http://192.168.0.1:8080/category/delete", parameters);
            return response;
        }


    }
}
