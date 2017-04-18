package com.fs.ps.put.finansominator.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.fs.ps.put.finansominator.R;

public class BudgetViewActivity extends AppCompatActivity {

    String budgetName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget_view);
    }


    public void goToTransactions(View view)
    {
        Intent intent = new Intent(this,TransactionsActivity.class);
        intent.putExtra("Name",budgetName);
        startActivity(intent);
    }
}
