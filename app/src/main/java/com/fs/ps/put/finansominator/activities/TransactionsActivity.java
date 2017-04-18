package com.fs.ps.put.finansominator.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.fs.ps.put.finansominator.R;
import com.fs.ps.put.finansominator.listAdapters.TransactionListAdapter;
import com.fs.ps.put.finansominator.listAdapters.beans.TransactionBean;
import com.fs.ps.put.finansominator.utils.FontManager;

import java.util.ArrayList;
import java.util.List;

public class TransactionsActivity extends AppCompatActivity {

    ListView transactionsListView;
    ArrayAdapter transactionsListAdapter;
    List<TransactionBean> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);
        initTransactionList();
        TextView fabText = (TextView) findViewById(R.id.FABTransactionText);
        Typeface typeface= FontManager.getTypeface(getApplicationContext(),FontManager.FONTAWESOME);
        fabText.setTypeface(typeface);
        getIntent().getStringExtra("Name");
    }

    void initTransactionList(){
        transactionsListView = (ListView)findViewById(R.id.transactionListView);
        data = getTransactionsData();
        fill(data);
        transactionsListAdapter = new TransactionListAdapter(this,R.layout.transaction_row,data);
        transactionsListView.setAdapter(transactionsListAdapter);
    }

    ArrayList<TransactionBean> getTransactionsData(){
        return new ArrayList<TransactionBean>();
    }

    void fill(List<TransactionBean> data){
        data.add(new TransactionBean("pierwszy", "75","Wczoraj","pierdoly"));
        data.add(new TransactionBean("drugi", "500","jutro","choleraWie"));
        data.add(new TransactionBean("trzeci", "75","15-12-2016","żarcie"));
    }

    public void addTransaction(View view){
      /*  AddTransactionDialog dialog = new AddTransactionDialog();
        dialog.show(getFragmentManager(),"AddTransactionDialog");*/


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View viewInflated = inflater.inflate(R.layout.add_transaction_dialog, null);
        final EditText budget = (EditText) viewInflated.findViewById(R.id.transactionBudgetDialogTxt);
        final EditText amount = (EditText) viewInflated.findViewById(R.id.transactionAmountDialogTxt);
        final EditText date = (EditText) viewInflated.findViewById(R.id.transactionDateDialogTxt);
        final EditText category = (EditText) viewInflated.findViewById(R.id.transactionCategoryDialogTxt);
        builder.setTitle("Add new transaction").
                setView(viewInflated)
                // Add action buttons
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Log.i("TRANSACTION_BUDGET", budget.getText().toString());
                        Log.i("TRANSACTION_AMOUNT", amount.getText().toString());
                        Log.i("TRANSACTION_CATEGORY", category.getText().toString());
                        Log.i("TRANSACTION_DATE", date.getText().toString());
                        data.add(new TransactionBean(budget.getText().toString(), amount.getText().toString(),date.getText().toString(),category.getText().toString()));
                        transactionsListAdapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        Dialog dialog = builder.create();
        dialog.show();





    }

}
