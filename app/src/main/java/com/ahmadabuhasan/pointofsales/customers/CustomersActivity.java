package com.ahmadabuhasan.pointofsales.customers;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.ahmadabuhasan.pointofsales.databinding.ActivityCustomersBinding;
import com.ahmadabuhasan.pointofsales.utils.BaseActivity;
import com.ahmadabuhasan.pointofsales.utils.Utils;
import com.ajts.androidmads.library.SQLiteToExcel;
import com.obsez.android.lib.filechooser.ChooserDialog;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

/*
 * Created by Ahmad Abu Hasan (C) 2022
 */

public class CustomersActivity extends BaseActivity {

    private ActivityCustomersBinding binding;
    ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCustomersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        new Utils().interstitialAdsShow(this);

        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.all_customer);

        this.binding.customerRecyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        this.binding.customerRecyclerview.setHasFixedSize(true);

        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);
        databaseAccess.open();
        List<HashMap<String, String>> customerData = databaseAccess.getCustomers();
        Log.d("data", "" + customerData.size());
        if (customerData.size() <= 0) {
            Toasty.info(this, R.string.no_customer_found, Toasty.LENGTH_SHORT).show();
            this.binding.ivNoCustomer.setImageResource(R.drawable.no_data);
        } else {
            this.binding.ivNoCustomer.setVisibility(View.GONE);

            CustomerAdapter adapter = new CustomerAdapter(this, customerData);
            this.binding.customerRecyclerview.setAdapter(adapter);
        }

        this.binding.etCustomerSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                databaseAccess.open();
                List<HashMap<String, String>> searchCustomerList = databaseAccess.searchCustomers(charSequence.toString());
                if (searchCustomerList.size() <= 0) {
                    binding.customerRecyclerview.setVisibility(View.GONE);
                    binding.ivNoCustomer.setVisibility(View.VISIBLE);
                    binding.ivNoCustomer.setImageResource(R.drawable.no_data);
                } else {
                    binding.ivNoCustomer.setVisibility(View.GONE);
                    binding.customerRecyclerview.setVisibility(View.VISIBLE);

                    CustomerAdapter adapter = new CustomerAdapter(CustomersActivity.this, searchCustomerList);
                    binding.customerRecyclerview.setAdapter(adapter);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        this.binding.fabAdd.setOnClickListener(view -> startActivity(new Intent(CustomersActivity.this, AddCustomersActivity.class)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_customer_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if (item.getItemId() == R.id.menu_export_customer) {
            folderChooser();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    public void folderChooser() {
        new ChooserDialog(this)
                .displayPath(true)
                .withFilter(true, false)
                .withChosenListener((dir, dirFile) -> {
                    CustomersActivity.this.onExport(dir);
                    Log.d("path", dir);
                }).build().show();
    }

    public void onExport(String path) {
        File file = new File(path);
        if (!file.exists()) {
            //noinspection ResultOfMethodCallIgnored
            file.mkdirs();
        }
        SQLiteToExcel sqLiteToExcel = new SQLiteToExcel(getApplicationContext(), DatabaseOpenHelper.DATABASE_NAME, path);
        sqLiteToExcel.exportSingleTable(Constant.customers, "customers.xls", new SQLiteToExcel.ExportListener() {
            @Override
            public void onStart() {
                CustomersActivity.this.loading = new ProgressDialog(CustomersActivity.this);
                CustomersActivity.this.loading.setMessage(CustomersActivity.this.getString(R.string.data_exporting_please_wait));
                CustomersActivity.this.loading.setCancelable(false);
                CustomersActivity.this.loading.show();
            }

            @Override
            public void onCompleted(String filePath) {
                Handler mHand = new Handler();
                mHand.postDelayed(() -> {
                    CustomersActivity.this.loading.dismiss();
                    Toasty.success(CustomersActivity.this, R.string.data_successfully_exported, Toasty.LENGTH_SHORT).show();
                }, 5000L);
            }

            @Override
            public void onError(Exception e) {
                CustomersActivity.this.loading.dismiss();
                Toasty.error(CustomersActivity.this, R.string.data_export_fail, Toasty.LENGTH_SHORT).show();
                Log.d("Error", e.toString());
            }
        });
    }
}