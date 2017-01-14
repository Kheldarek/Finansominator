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

import java.util.List;

/**
 * Created by Kheldar on 14-Jan-17.
 */


public class GroupBudgetAdapter extends ArrayAdapter<GroupBudgetBean>{

    Context context;
    int resource;
    List<GroupBudgetBean> objects;

    public GroupBudgetAdapter(Context context, int resource, List<GroupBudgetBean> objects) {
        super(context, resource, objects);

        this.context = context;
        this.resource= resource;
        this.objects = objects;

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
            rowHolder.owner = (TextView)row.findViewById(R.id.groupBudgetOwnerTxt);
            rowHolder.members = (TextView)row.findViewById(R.id.groupBudgetMembersTxt);
            rowHolder.balance = (TextView)row.findViewById(R.id.groupBudgetBalanceTxt);
            row.setTag(rowHolder);
        }
        else{
            rowHolder = (RowHolder)row.getTag();
        }

        GroupBudgetBean object = objects.get(position);
        rowHolder.name.setText(String.format("Name: %s", object.name));
        rowHolder.owner.setText(String.format("Owner: %s", object.owner));
        rowHolder.members.setText(String.format("Members: %s", object.members));
        rowHolder.balance.setText(String.format("Balance: %s", object.balance));

        return row;

    }


    static class RowHolder{
        TextView name;
        TextView owner;
        TextView members;
        TextView balance;
    }
}
