package com.ahmadabuhasan.pointofsales.report;

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

import com.ahmadabuhasan.pointofsales.Constant;
import com.ahmadabuhasan.pointofsales.R;
import com.ahmadabuhasan.pointofsales.database.DatabaseAccess;
import com.ahmadabuhasan.pointofsales.database.DatabaseOpenHelper;
import com.ahmadabuhasan.pointofsales.databinding.ActivitySalesReportBinding;
import com.ahmadabuhasan.pointofsales.utils.BaseActivity;
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

public class SalesReportActivity extends BaseActivity {

    private ActivitySalesReportBinding binding;

    ProgressDialog loading;
    DecimalFormat decimalFormat = new DecimalFormat("#0.00");
    DatabaseAccess databaseAccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySalesReportBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.all_sales);

        this.binding.ivNoData.setVisibility(View.GONE);
        this.binding.tvNoData.setVisibility(View.GONE);

        this.binding.salesReportRecyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        this.binding.salesReportRecyclerview.setHasFixedSize(true);

        databaseAccess = DatabaseAccess.getInstance(this);
        databaseAccess.open();
        ArrayList<HashMap<String, String>> allSalesItems = databaseAccess.getAllSalesItems();
        if (allSalesItems.size() <= 0) {
            Toasty.info(this, R.string.no_data_found, Toasty.LENGTH_SHORT).show();
            this.binding.salesReportRecyclerview.setVisibility(View.GONE);
            this.binding.tvTotalPrice.setVisibility(View.GONE);
            this.binding.tvNoData.setVisibility(View.VISIBLE);
            this.binding.ivNoData.setVisibility(View.VISIBLE);
            this.binding.ivNoData.setImageResource(R.drawable.not_found);
        } else {
            SalesReportAdapter adapter = new SalesReportAdapter(this, allSalesItems);
            this.binding.salesReportRecyclerview.setAdapter(adapter);
        }

        databaseAccess.open();
        String currency = databaseAccess.getCurrency();
        databaseAccess.open();
        double sub_total = databaseAccess.getTotalOrderPrice("all");
        this.binding.tvTotalPrice.setText(String.format("%s%s%s", getString(R.string.total_sales), currency, this.decimalFormat.format(sub_total)));

        databaseAccess.open();
        double get_tax = databaseAccess.getTotalTax("all");
        this.binding.tvTotalTax.setText(String.format("%s(+) : %s%s", getString(R.string.tax), currency, this.decimalFormat.format(get_tax)));

        databaseAccess.open();
        double get_discount = databaseAccess.getTotalDiscount("all");
        this.binding.tvTotalDiscount.setText(String.format("%s(-) : %s%s", getString(R.string.total_discount), currency, this.decimalFormat.format(get_discount)));

        double net_sales = (sub_total + get_tax) - get_discount;
        this.binding.tvNetSales.setText(String.format("%s: %s%s", getString(R.string.net_sales), currency, this.decimalFormat.format(net_sales)));
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
        Log.d("TYPE", type);
        ArrayList<HashMap<String, String>> salesReport = databaseAccess.getSalesReport(type);
        if (salesReport.size() <= 0) {
            Toasty.info(this, R.string.no_data_found, Toasty.LENGTH_SHORT).show();
            this.binding.salesReportRecyclerview.setVisibility(View.GONE);
            this.binding.tvTotalPrice.setVisibility(View.GONE);
            this.binding.tvNoData.setVisibility(View.VISIBLE);
            this.binding.ivNoData.setVisibility(View.VISIBLE);
            this.binding.ivNoData.setImageResource(R.drawable.not_found);
        } else {
            this.binding.tvNoData.setVisibility(View.GONE);
            this.binding.ivNoData.setVisibility(View.GONE);
            this.binding.tvTotalPrice.setVisibility(View.VISIBLE);
            this.binding.salesReportRecyclerview.setVisibility(View.VISIBLE);

            SalesReportAdapter adapter1 = new SalesReportAdapter(this, salesReport);
            this.binding.salesReportRecyclerview.setAdapter(adapter1);
        }

        databaseAccess.open();
        String currency = databaseAccess.getCurrency();

        databaseAccess.open();
        double sub_total = databaseAccess.getTotalOrderPrice(type);
        this.binding.tvTotalPrice.setText(String.format("%s%s%s", getString(R.string.total_sales), currency, this.decimalFormat.format(sub_total)));

        databaseAccess.open();
        double get_tax = databaseAccess.getTotalTax(type);
        this.binding.tvTotalTax.setText(String.format("%s(+) : %s%s", getString(R.string.tax), currency, this.decimalFormat.format(get_tax)));

        databaseAccess.open();
        double get_discount = databaseAccess.getTotalDiscount(type);
        this.binding.tvTotalDiscount.setText(String.format("%s(-) : %s%s", getString(R.string.discount), currency, this.decimalFormat.format(get_discount)));

        double net_sale = (sub_total + get_tax) - get_discount;
        this.binding.tvNetSales.setText(String.format("%s: %s%s", getString(R.string.net_sales), currency, this.decimalFormat.format(net_sale)));
    }

    public void folderChooser() {
        new ChooserDialog((Activity) this)
                .displayPath(true)
                .withFilter(true, false, new String[0])
                .withChosenListener(new ChooserDialog.Result() {
                    @Override
                    public void onChoosePath(String dir, File dirFile) {
                        SalesReportActivity.this.onExport(dir);
                        Log.d("PATH", dir);
                    }
                }).build().show();
    }

    public void onExport(String path) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        SQLiteToExcel sqLiteToExcel = new SQLiteToExcel(getApplicationContext(), DatabaseOpenHelper.DATABASE_NAME, path);
        sqLiteToExcel.exportSingleTable(Constant.orderDetails, "order_details.xls", new SQLiteToExcel.ExportListener() {
            @Override
            public void onStart() {
                SalesReportActivity.this.loading = new ProgressDialog(SalesReportActivity.this);
                SalesReportActivity.this.loading.setMessage(SalesReportActivity.this.getString(R.string.data_exporting_please_wait));
                SalesReportActivity.this.loading.setCancelable(false);
                SalesReportActivity.this.loading.show();
            }

            @Override
            public void onCompleted(String filePath) {
                Handler mHand = new Handler();
                mHand.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        SalesReportActivity.this.loading.dismiss();
                        Toasty.success(SalesReportActivity.this, R.string.data_successfully_exported, Toasty.LENGTH_SHORT).show();
                    }
                }, 5000L);
            }

            @Override
            public void onError(Exception e) {
                SalesReportActivity.this.loading.dismiss();
                Toasty.error(SalesReportActivity.this, R.string.data_export_fail, Toasty.LENGTH_SHORT).show();
            }
        });
    }
}