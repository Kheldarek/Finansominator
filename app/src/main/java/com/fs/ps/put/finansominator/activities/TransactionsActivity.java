package com.fs.ps.put.finansominator.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.fs.ps.put.finansominator.R;
import com.fs.ps.put.finansominator.dialogs.AddTransactionDialog;
import com.fs.ps.put.finansominator.listAdapters.TransactionListAdapter;
import com.fs.ps.put.finansominator.listAdapters.beans.TransactionBean;

public class TransactionsActivity extends AppCompatActivity {

    ListView transactionsListView;
    ArrayAdapter transactionsListAdapter;
    TransactionBean data[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);
        initTransactionList();
    }

    void initTransactionList(){
        transactionsListView = (ListView)findViewById(R.id.transactionListView);
        data = getTransactionsData();
        fill(data);
        transactionsListAdapter = new TransactionListAdapter(this,R.layout.transaction_row,data);
        transactionsListView.setAdapter(transactionsListAdapter);
    }

    TransactionBean[] getTransactionsData(){
        return new TransactionBean[3];
    }

    void fill(TransactionBean data[]){
        data[0] =(new TransactionBean("pierwszy", "75","Wczoraj","pierdoly"));
        data[1] = (new TransactionBean("drugi", "500","jutro","choleraWie"));
        data[2] = (new TransactionBean("trzeci", "75","15-12-2016","żarcie"));
    }

    public void addTransaction(View view){
        AddTransactionDialog dialog = new AddTransactionDialog();
        dialog.show(getFragmentManager(),"AddTransactionDialog");
    }

}
