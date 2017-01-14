package com.fs.ps.put.finansominator.listAdapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.fs.ps.put.finansominator.R;
import com.fs.ps.put.finansominator.listAdapters.beans.TransactionBean;

import java.util.List;

/**
 * Created by Kheldar on 14-Jan-17.
 */

public class TransactionListAdapter extends ArrayAdapter<TransactionBean> {

    Context context;
    int resource;
    TransactionBean objects[];

    public TransactionListAdapter(Context context, int resource, TransactionBean objects[]) {
        super(context, resource, objects);

        this.context = context;
        this.resource = resource;
        this.objects = objects;
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

            row.setTag(rowHolder);
        } else {
            rowHolder = (RowHolder) row.getTag();
        }

        TransactionBean object = objects[position];
        rowHolder.budget.setText(String.format("Budget: %s", object.budget));
        rowHolder.amount.setText(String.format("Amount: %s" ,object.amount));
        rowHolder.date.setText(String.format("Date: %s" ,object.date));
        rowHolder.category.setText(String.format("Category: %s", object.category));

        return row;

    }


    static class RowHolder {
        TextView budget;
        TextView amount;
        TextView date;
        TextView category;
    }
}