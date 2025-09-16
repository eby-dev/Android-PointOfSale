package com.ahmadabuhasan.pointofsale.report;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ahmadabuhasan.pointofsale.Constant;
import com.ahmadabuhasan.pointofsale.R;
import com.ahmadabuhasan.pointofsale.database.DatabaseAccess;
import com.ahmadabuhasan.pointofsale.database.DatabaseOpenHelper;
import com.ahmadabuhasan.pointofsale.databinding.ActivityExpenseReportBinding;
import com.ahmadabuhasan.pointofsale.expense.ExpenseAdapter;
import com.ahmadabuhasan.pointofsale.utils.BaseActivity;
import com.ajts.androidmads.library.SQLiteToExcel;
import com.obsez.android.lib.filechooser.ChooserDialog;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

/*
 * Created by Ahmad Abu Hasan (C) 2022
 */

public class ExpenseReportActivity extends BaseActivity {

    private ActivityExpenseReportBinding binding;

    ProgressDialog loading;
    DecimalFormat decimalFormat = new DecimalFormat("#0.00");
    DatabaseAccess databaseAccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityExpenseReportBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.all_expense);

        this.binding.ivNoData.setVisibility(View.GONE);
        this.binding.tvNoData.setVisibility(View.GONE);

        this.binding.expenseReportRecyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        this.binding.expenseReportRecyclerview.setHasFixedSize(true);

        databaseAccess = DatabaseAccess.getInstance(this);
        databaseAccess.open();
        ArrayList<HashMap<String, String>> allExpense = databaseAccess.getAllExpense();
        if (allExpense.size() <= 0) {
            Toasty.info(this, R.string.no_data_found, Toasty.LENGTH_SHORT).show();
            this.binding.expenseReportRecyclerview.setVisibility(View.GONE);
            this.binding.tvTotalPrice.setVisibility(View.GONE);
            this.binding.tvNoData.setVisibility(View.VISIBLE);
            this.binding.ivNoData.setVisibility(View.VISIBLE);
            this.binding.ivNoData.setImageResource(R.drawable.not_found);
        } else {
            ExpenseAdapter adapter = new ExpenseAdapter(this, allExpense);
            this.binding.expenseReportRecyclerview.setAdapter(adapter);
        }

        databaseAccess.open();
        String currency = databaseAccess.getCurrency();

        databaseAccess.open();
        double total_price = databaseAccess.getTotalExpense("all");
        this.binding.tvTotalPrice.setText(String.format("%s%s%s", getString(R.string.total_expense), currency, this.decimalFormat.format(total_price)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.all_sales_menu, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        /*switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.menu_all_sales:
                getReport("all");
                return true;
            case R.id.menu_daily:
                getReport(Constant.DAILY);
                return true;
            case R.id.menu_monthly:
                getReport(Constant.MONTHLY);
                return true;
            case R.id.menu_yearly:
                getReport(Constant.YEARLY);
                return true;
            case R.id.menu_export_data:
                folderChooser();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }*/

        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        } else if (id == R.id.menu_all_sales) {
            getReport("all");
            return true;
        } else if (id == R.id.menu_daily) {
            getReport(Constant.DAILY);
            return true;
        } else if (id == R.id.menu_monthly) {
            getReport(Constant.MONTHLY);
            return true;
        } else if (id == R.id.menu_yearly) {
            getReport(Constant.YEARLY);
            return true;
        } else if (id == R.id.menu_export_data) {
            folderChooser();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void getReport(String type) {
        databaseAccess.open();
        ArrayList<HashMap<String, String>> expenseReport = databaseAccess.getExpenseReport(type);
        if (expenseReport.size() <= 0) {
            Toasty.info(this, R.string.no_data_found, Toasty.LENGTH_SHORT).show();
            this.binding.expenseReportRecyclerview.setVisibility(View.GONE);
            this.binding.tvTotalPrice.setVisibility(View.GONE);
            this.binding.tvNoData.setVisibility(View.VISIBLE);
            this.binding.ivNoData.setVisibility(View.VISIBLE);
            this.binding.ivNoData.setImageResource(R.drawable.not_found);
        } else {
            this.binding.tvNoData.setVisibility(View.GONE);
            this.binding.ivNoData.setVisibility(View.GONE);
            this.binding.tvTotalPrice.setVisibility(View.VISIBLE);
            this.binding.expenseReportRecyclerview.setVisibility(View.VISIBLE);

            ExpenseAdapter adapter1 = new ExpenseAdapter(this, expenseReport);
            this.binding.expenseReportRecyclerview.setAdapter(adapter1);
        }

        databaseAccess.open();
        String currency = databaseAccess.getCurrency();

        databaseAccess.open();
        double total_price = databaseAccess.getTotalExpense(type);
        this.binding.tvTotalPrice.setText(String.format("%s%s%s", getString(R.string.total_expense), currency, this.decimalFormat.format(total_price)));
    }

    public void folderChooser() {
        new ChooserDialog((Activity) this)
                .displayPath(true)
                .withFilter(true, false, new String[0])
                .withChosenListener(new ChooserDialog.Result() {
                    @Override
                    public void onChoosePath(String dir, File dirFile) {
                        ExpenseReportActivity.this.onExport(dir);
                        Log.d("path", dir);
                    }
                }).build().show();
    }

    public void onExport(String path) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        SQLiteToExcel sqLiteToExcel = new SQLiteToExcel(getApplicationContext(), DatabaseOpenHelper.DATABASE_NAME, path);
        sqLiteToExcel.exportSingleTable(Constant.expense, "expense.xls", new SQLiteToExcel.ExportListener() {
            @Override
            public void onStart() {
                ExpenseReportActivity.this.loading = new ProgressDialog(ExpenseReportActivity.this);
                ExpenseReportActivity.this.loading.setMessage(ExpenseReportActivity.this.getString(R.string.data_exporting_please_wait));
                ExpenseReportActivity.this.loading.setCancelable(false);
                ExpenseReportActivity.this.loading.show();
            }

            @Override
            public void onCompleted(String filePath) {
                Handler mHand = new Handler();
                mHand.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ExpenseReportActivity.this.loading.dismiss();
                        Toasty.success(ExpenseReportActivity.this, R.string.data_successfully_exported, Toasty.LENGTH_SHORT).show();
                    }
                }, 5000L);
            }

            @Override
            public void onError(Exception e) {
                ExpenseReportActivity.this.loading.dismiss();
                Toasty.error(ExpenseReportActivity.this, R.string.data_export_fail, Toasty.LENGTH_SHORT).show();
            }
        });
    }
}