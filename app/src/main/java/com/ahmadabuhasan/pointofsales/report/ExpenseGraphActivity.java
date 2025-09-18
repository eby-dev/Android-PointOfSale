package com.ahmadabuhasan.pointofsales.report;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.ahmadabuhasan.pointofsales.Constant;
import com.ahmadabuhasan.pointofsales.R;
import com.ahmadabuhasan.pointofsales.database.DatabaseAccess;
import com.ahmadabuhasan.pointofsales.databinding.ActivityExpenseGraphBinding;
import com.ahmadabuhasan.pointofsales.utils.BaseActivity;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.whiteelephant.monthpicker.MonthPickerDialog;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

/*
 * Created by Ahmad Abu Hasan (C) 2022
 */

public class ExpenseGraphActivity extends BaseActivity {

    private ActivityExpenseGraphBinding binding;

    int mYear;
    DecimalFormat decimalFormat = new DecimalFormat("#0.00");
    DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityExpenseGraphBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.monthly_expense_in_graph);

        this.binding.barChart.setDrawBarShadow(false);
        this.binding.barChart.setDrawValueAboveBar(true);
        this.binding.barChart.setDrawGridBackground(true);
        this.binding.barChart.setMaxVisibleValueCount(50);
        this.binding.barChart.setPinchZoom(false);

        String currentYear = new SimpleDateFormat("yyyy", Locale.getDefault()).format(new Date());
        this.binding.tvSelectYear.setText(String.format("%s %s", getString(R.string.year), currentYear));
        int parseInt = Integer.parseInt(currentYear);
        this.mYear = parseInt;
        getGraphData(parseInt);
    }

    public void getGraphData(int mYear) {
        String[] monthList = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        String[] monthNumber = {"00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};

        ArrayList<BarEntry> barEntries = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            databaseAccess.open();
            String month = monthNumber[i];
            barEntries.add(new BarEntry(i, databaseAccess.getMonthlyExpenseAmount(month, "" + mYear)));
        }

        XAxis xAxis = this.binding.barChart.getXAxis();
        xAxis.setCenterAxisLabels(true);
        xAxis.setGranularity(1.0f);
        xAxis.setGranularityEnabled(true);
        xAxis.setLabelCount(12);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(monthList));

        BarDataSet barDataSet = new BarDataSet(barEntries, getString(R.string.monthly_expense_report));
        barDataSet.setColors(ColorTemplate.LIBERTY_COLORS);
        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(0.9f);

        this.binding.barChart.setData(barData);
        this.binding.barChart.setScaleEnabled(false);
        this.binding.barChart.invalidate();
        this.binding.barChart.notifyDataSetChanged();

        databaseAccess.open();
        String currency = databaseAccess.getCurrency();
        databaseAccess.open();
        double totalExpense = databaseAccess.getTotalExpenseForGraph(Constant.YEARLY, mYear);
        this.binding.tvTotalSales.setText(String.format("%s%s%s", getString(R.string.total_expense), currency, this.decimalFormat.format(totalExpense)));

        this.binding.layoutYear.setOnClickListener(view -> this.chooseYearOnly());
    }

    private void chooseYearOnly() {
        this.binding.tvSelectYear.setOnClickListener(view -> {
            MonthPickerDialog.Builder builder = new MonthPickerDialog.Builder(this, (selectedMonth, selectedYear) -> {
                this.binding.tvSelectYear.setText(String.format("%s %s", getString(R.string.year), selectedYear));
                this.mYear = selectedYear;
                this.getGraphData(this.mYear);
            }, this.mYear, 0);
            builder.showYearOnly()
                    .setTitle(ExpenseGraphActivity.this.getString(R.string.select_year)).setYearRange(1990, 2099)
                    .build().show();
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() != android.R.id.home) {
            return super.onOptionsItemSelected(item);
        }
        finish();
        return true;
    }
}