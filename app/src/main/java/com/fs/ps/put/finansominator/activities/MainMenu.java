package com.fs.ps.put.finansominator.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.fs.ps.put.finansominator.R;

public class MainMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
    }

   public void goToPersonalBudget(View view)
    {
        Intent intent = new Intent(this,PersonalBudgetActivity.class);
        startActivity(intent);
    }

   public void goToGroupBudget(View view)
    {
        Intent intent = new Intent(this,GroupBudgetActivity.class);
        startActivity(intent);
    }

    public void goToTransactions(View view)
    {
        Intent intent = new Intent(this,TransactionsActivity.class);
        startActivity(intent);
    }
    public void goToReports(View view)
    {
        Intent intent = new Intent(this,ReportsActivity.class);
        startActivity(intent);
    }

   public void goToStats(View view)
    {
        Intent intent = new Intent(this,StatsActivity.class);
        startActivity(intent);
    }


}
