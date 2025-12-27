package com.ahmadabuhasan.pointofsales.suppliers;

import android.app.Activity;
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
import com.ahmadabuhasan.pointofsales.databinding.ActivitySuppliersBinding;
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

public class SuppliersActivity extends BaseActivity {

    private ActivitySuppliersBinding binding;

    ProgressDialog loading;
    DatabaseAccess databaseAccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySuppliersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //new Utils().interstitialAdsShow(this);

        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.all_suppliers);

        this.binding.supplierRecyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        this.binding.supplierRecyclerview.setHasFixedSize(true);

        databaseAccess = DatabaseAccess.getInstance(this);
        databaseAccess.open();
        List<HashMap<String, String>> supplierData = databaseAccess.getSuppliers();
        Log.d("data", "" + supplierData.size());
        if (supplierData.size() <= 0) {
            Toasty.info(this, R.string.no_suppliers_found, Toasty.LENGTH_SHORT).show();
            this.binding.ivNoSupplier.setImageResource(R.drawable.no_data);
        } else {
            this.binding.ivNoSupplier.setVisibility(View.GONE);

            SupplierAdapter adapter = new SupplierAdapter(this, supplierData);
            this.binding.supplierRecyclerview.setAdapter(adapter);
        }

        this.binding.etSupplierSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                databaseAccess.open();
                List<HashMap<String, String>> searchSupplier = databaseAccess.searchSuppliers(charSequence.toString());
                if (searchSupplier.size() <= 0) {
                    binding.supplierRecyclerview.setVisibility(View.GONE);
                    binding.ivNoSupplier.setVisibility(View.VISIBLE);
                    binding.ivNoSupplier.setImageResource(R.drawable.no_data);
                } else {
                    binding.ivNoSupplier.setVisibility(View.GONE);
                    binding.supplierRecyclerview.setVisibility(View.VISIBLE);

                    SupplierAdapter adapter1 = new SupplierAdapter(SuppliersActivity.this, searchSupplier);
                    binding.supplierRecyclerview.setAdapter(adapter1);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        this.binding.fabAdd.setOnClickListener(view -> this.startActivity(new Intent(SuppliersActivity.this, AddSuppliersActivity.class)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.export_suppliers_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if (item.getItemId() == R.id.menu_export_supplier) {
            folderChooser();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    public void folderChooser() {
        new ChooserDialog((Activity) this)
                .displayPath(true)
                .withFilter(true, false, new String[0])
                .withChosenListener(new ChooserDialog.Result() {
                    @Override
                    public void onChoosePath(String dir, File dirFile) {
                        SuppliersActivity.this.onExport(dir);
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
        sqLiteToExcel.exportSingleTable(Constant.suppliers, "suppliers.xls", new SQLiteToExcel.ExportListener() {
            @Override
            public void onStart() {
                SuppliersActivity.this.loading = new ProgressDialog(SuppliersActivity.this);
                SuppliersActivity.this.loading.setMessage(SuppliersActivity.this.getString(R.string.data_exporting_please_wait));
                SuppliersActivity.this.loading.setCancelable(false);
                SuppliersActivity.this.loading.show();
            }

            @Override
            public void onCompleted(String filePath) {
                Handler mHand = new Handler();
                mHand.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        SuppliersActivity.this.loading.dismiss();
                        Toasty.success(SuppliersActivity.this, R.string.data_successfully_exported, Toasty.LENGTH_SHORT).show();
                    }
                }, 5000L);
            }

            @Override
            public void onError(Exception e) {
                SuppliersActivity.this.loading.dismiss();
                Toasty.error(SuppliersActivity.this, R.string.data_export_fail, Toasty.LENGTH_SHORT).show();
            }
        });
    }
}