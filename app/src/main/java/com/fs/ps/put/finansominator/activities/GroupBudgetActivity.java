package com.fs.ps.put.finansominator.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
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
        initGroupBudgetList();
    }

    private void initGroupBudgetList() {
        groupBudgetList = (ListView) findViewById(R.id.groupBudgetsList);
        data = getGroupBudgets();
        fill(data);
        listAdapter = new GroupBudgetAdapter(this, R.layout.group_budget_row, data);
        groupBudgetList.setAdapter(listAdapter);

    }

    List<GroupBudgetBean> getGroupBudgets() {
        return new ArrayList<>();
    }

    void fill(List<GroupBudgetBean> data) {
        data.add(new GroupBudgetBean("pierwszy", "andrzej", "sliwka,chudy", "+75.48"));
    }

   public void addNewBudgetEvent(View view) {
        AddGroupBudgetDialog dialog = new AddGroupBudgetDialog();
        dialog.show(getFragmentManager(),"AddGroupBudgetDialog");
    }

}
