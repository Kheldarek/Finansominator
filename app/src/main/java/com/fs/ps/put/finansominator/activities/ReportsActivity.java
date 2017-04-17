package com.fs.ps.put.finansominator.activities;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.fs.ps.put.finansominator.R;
import com.fs.ps.put.finansominator.formatters.ZlotyFormatter;
import com.fs.ps.put.finansominator.utils.FontManager;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportsActivity extends AppCompatActivity {

    public static final String SHOW_ALL = "All Budgets";
    Map<String, Map<String, Float>> dataSet;
    PieChart pieChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);
        TextView fabText = (TextView) findViewById(R.id.editChartFABText);
        fabText.setTypeface(FontManager.getTypeface(getApplicationContext(), FontManager.FONTAWESOME));
        pieChart = (PieChart) findViewById(R.id.pieChart);
        initializeDataSet();
        buildPieChartDataSet(SHOW_ALL);
        configureChart();
    }

    private void initializeDataSet() {

        dataSet = new HashMap<>();

        Map<String, Float> budget1Map = new HashMap<>();
        Map<String, Float> budget2Map = new HashMap<>();

        budget1Map.put("Zarcie", 200.00f);
        budget1Map.put("Szlugi", 121.00f);
        budget1Map.put("Planszowki", 54.87f);
        budget1Map.put("Piwo", 71.11f);
        budget2Map.put("Woda", 89.73f);
        budget2Map.put("Lalki", 49.71f);
        budget2Map.put("Cokolwiek", 91.87f);
        budget2Map.put("Yerba", 143.78f);
        budget2Map.put("Ciuchy", 491.61f);

        dataSet.put("Budget1", budget1Map);
        dataSet.put("Budget2", budget2Map);
    }

    private void configureChart() {


        pieChart.getLegend().setEnabled(false);
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText("All budgets categories");
        pieChart.animateXY(2000, 2000);
        //pieChart.setUsePercentValues(true);


    }

    private void buildPieChartDataSet(String budgetName) {
        List<PieEntry> pieEntries = new ArrayList<>();

        if (budgetName.equals(SHOW_ALL)) {
            for (Map<String, Float> map : dataSet.values()) {
                for (Map.Entry<String, Float> entry : map.entrySet()) {
                    pieEntries.add(new PieEntry(entry.getValue(), entry.getKey()));
                }
            }
        } else {
            Map<String, Float> budgetData = dataSet.get(budgetName);
            for (Map.Entry<String, Float> entry : budgetData.entrySet()) {
                pieEntries.add(new PieEntry(entry.getValue(), entry.getKey()));
            }
        }

        PieDataSet pieDataSet = new PieDataSet(pieEntries, "");
        pieDataSet.setColors(ColorTemplate.JOYFUL_COLORS);

        PieData pieData = new PieData(pieDataSet);
        pieData.setValueTextColor(Color.DKGRAY);
        pieData.setValueTextSize(11);
        pieData.setValueFormatter(new ZlotyFormatter());
        pieChart.setData(pieData);
        pieChart.setCenterText(budgetName);
        pieData.notifyDataChanged();
        pieChart.invalidate();

    }

    public void editChart(View view) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(ReportsActivity.this);
        dialog.setTitle("Choose budget to show!");

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                ReportsActivity.this, android.R.layout.select_dialog_singlechoice);

        adapter.add(SHOW_ALL);
        adapter.addAll(dataSet.keySet());


        dialog.setAdapter(adapter, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                buildPieChartDataSet(adapter.getItem(which));
            }
        });
        dialog.setNegativeButton("Cancel", null);
        dialog.show();
    }
}
