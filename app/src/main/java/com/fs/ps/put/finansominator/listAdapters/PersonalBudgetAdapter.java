package com.fs.ps.put.finansominator.listAdapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.fs.ps.put.finansominator.R;
import com.fs.ps.put.finansominator.listAdapters.beans.GroupBudgetBean;
import com.fs.ps.put.finansominator.listAdapters.beans.PersonalBudgetBean;

import java.util.List;

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
            rowHolder.name = (TextView)row.findViewById(R.id.groupBudgetNameTxt);
            rowHolder.balance = (TextView)row.findViewById(R.id.groupBudgetBalanceTxt);
            row.setTag(rowHolder);
        }
        else{
            rowHolder = (RowHolder)row.getTag();
        }

        PersonalBudgetBean object = objects.get(position);
        rowHolder.name.setText(object.name);
        rowHolder.balance.setText(object.balance);

        return row;

    }


    static class RowHolder{
        TextView name;
        TextView balance;
    }
}
