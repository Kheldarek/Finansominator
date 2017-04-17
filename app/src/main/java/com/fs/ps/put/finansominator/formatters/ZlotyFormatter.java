package com.fs.ps.put.finansominator.formatters;


import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;

/**
 * Created by Kheldar on 17-Apr-17.
 */

public class ZlotyFormatter implements IValueFormatter {

    private DecimalFormat mFormat;

    public ZlotyFormatter() {
        mFormat = new DecimalFormat("###,###,##0.00"); // use one decimal
    }

    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        // write your logic here
        return mFormat.format(value) + " zl"; //
    }
}
