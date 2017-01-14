package com.fs.ps.put.finansominator.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.fs.ps.put.finansominator.R;
import com.fs.ps.put.finansominator.listAdapters.GroupBudgetAdapter;
import com.fs.ps.put.finansominator.listAdapters.beans.GroupBudgetBean;

import java.util.ArrayList;
import java.util.List;

public class GroupBudgetActivity extends AppCompatActivity {

    ListView groupBudgetList;
    ArrayAdapter listAdapter;
    List<GroupBudgetBean> data;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_budget);
    }

    private void initGroupBudgetList()
    {
        groupBudgetList = (ListView)findViewById(R.id.groupBudgetsList);
        listAdapter = new GroupBudgetAdapter(this, R.layout.group_budget_row,data);

    }

    List<GroupBudgetBean> getGroupBudgets()
    {
        return new ArrayList<>();
    }

}
