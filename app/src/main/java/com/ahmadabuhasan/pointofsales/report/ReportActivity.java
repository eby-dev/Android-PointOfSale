package com.ahmadabuhasan.pointofsales.report;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.ahmadabuhasan.pointofsales.R;
import com.ahmadabuhasan.pointofsales.databinding.ActivityReportBinding;
import com.ahmadabuhasan.pointofsales.utils.BaseActivity;
import com.ahmadabuhasan.pointofsales.utils.Utils;

import java.util.Objects;

/*
 * Created by Ahmad Abu Hasan (C) 2022
 */

public class ReportActivity extends BaseActivity {

    private ActivityReportBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReportBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        new Utils().interstitialAdsShow(this);

        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.report);

        this.binding.cvSalesReport.setOnClickListener(view -> this.startActivity(new Intent(ReportActivity.this, SalesReportActivity.class)));
        this.binding.cvGraphReport.setOnClickListener(view -> this.startActivity(new Intent(ReportActivity.this, GraphReportActivity.class)));
        this.binding.cvExpenseReport.setOnClickListener(view -> this.startActivity(new Intent(ReportActivity.this, ExpenseReportActivity.class)));
        this.binding.cvGraphExpense.setOnClickListener(view -> this.startActivity(new Intent(ReportActivity.this, ExpenseGraphActivity.class)));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}