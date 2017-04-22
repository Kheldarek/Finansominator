package com.fs.ps.put.finansominator.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.fs.ps.put.finansominator.R;
import com.fs.ps.put.finansominator.utils.FontManager;

public class MainMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Typeface iconTypeface = FontManager.getTypeface(getApplicationContext(),FontManager.FONTAWESOME);
        setContentView(R.layout.activity_main_menu);
        Button personalBudgetbtn = (Button)findViewById(R.id.personalBudgetsBtn);
        Button reports = (Button)findViewById(R.id.reportsBtn);
        Button transactions = (Button)findViewById(R.id.transactionsBtn);
        Button userData = (Button) findViewById(R.id.userDataBtn);
        personalBudgetbtn.setTypeface(iconTypeface);
        reports.setTypeface(iconTypeface);
        transactions.setTypeface(iconTypeface);
        userData.setTypeface(iconTypeface);

    }

   public void goToPersonalBudget(View view)
    {
        Intent intent = new Intent(this,BudgetActivity.class);
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
        Intent intent = new Intent(this,UserActivity.class);
        startActivity(intent);
    }


}
