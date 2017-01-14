package com.fs.ps.put.finansominator.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.fs.ps.put.finansominator.R;
import com.fs.ps.put.finansominator.listAdapters.PersonalBudgetAdapter;
import com.fs.ps.put.finansominator.listAdapters.beans.PersonalBudgetBean;

import java.util.ArrayList;
import java.util.List;

public class PersonalBudgetActivity extends AppCompatActivity {


    ListView personalBudgetList;
    List<PersonalBudgetBean> data;
    ArrayAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_budget);

    }

    void initPersonalBudgetsList() {
        personalBudgetList = (ListView)findViewById(R.id.personalBudgetsList);
        data = getPersonalBudgetsData();
        listAdapter = new PersonalBudgetAdapter(this,R.layout.personal_budget_row,data);

    }

    List<PersonalBudgetBean> getPersonalBudgetsData() {
        return new ArrayList<>();
    }


}
