package com.fs.ps.put.finansominator.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.fs.ps.put.finansominator.R;
import com.fs.ps.put.finansominator.listAdapters.PersonalBudgetAdapter;
import com.fs.ps.put.finansominator.listAdapters.beans.PersonalBudgetBean;
import com.fs.ps.put.finansominator.utils.FontManager;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class BudgetActivity extends AppCompatActivity {


    ListView personalBudgetList;
    List<PersonalBudgetBean> data;
    ArrayAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_budget);
        initPersonalBudgetsList();
        TextView fabText = (TextView) findViewById(R.id.FABtext);
        Typeface typeface= FontManager.getTypeface(getApplicationContext(),FontManager.FONTAWESOME);
        fabText.setTypeface(typeface);

    }

    void initPersonalBudgetsList() {
        personalBudgetList = (ListView) findViewById(R.id.personalBudgetsList);
        data = getPersonalBudgetsData();
        fill(data);
        listAdapter = new PersonalBudgetAdapter(this, R.layout.personal_budget_row, data);
        personalBudgetList.setAdapter(listAdapter);
        personalBudgetList.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?>adapter,View v, int position,long i){
                PersonalBudgetBean item = (PersonalBudgetBean) adapter.getItemAtPosition(position);

                Intent intent = new Intent(getApplicationContext(),BudgetViewActivity.class);
                //based on item add info to intent
                startActivity(intent);
            }
        });;

    }

    List<PersonalBudgetBean> getPersonalBudgetsData() {
        return new ArrayList<>();
    }

    void fill(List<PersonalBudgetBean> data) {
        data.add(new PersonalBudgetBean("Na zarcie", "400.00"));
        data.add(new PersonalBudgetBean("Na piwo", "100.00"));
    }

    public void addNewBudget(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View viewInflated = inflater.inflate(R.layout.fragment_add_personal_budget_dialog, null);
        final TextView nameField = (TextView) viewInflated.findViewById(R.id.personalBudgetNameDialogTxt);
        final TextView ownerField = (TextView) viewInflated.findViewById(R.id.personalBudgetDialogOwnerName);
        final TextView categoriesField = (TextView) viewInflated.findViewById(R.id.personalBudgetDialogCategoriesTxt);
        builder.setTitle("Add new personal budget").
                setView(viewInflated)
                // Add action buttons
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Log.i("PERSONAL_BUDGET_NAME", nameField.getText().toString());
                        Log.i("PERSONAL_BUDGET_OWNER", ownerField.getText().toString());
                        Log.i("PERSONAL_BUDGET_CAT", categoriesField.getText().toString());
                        data.add(new PersonalBudgetBean(nameField.getText().toString(), "0.00"));
                        listAdapter.notifyDataSetChanged();
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
