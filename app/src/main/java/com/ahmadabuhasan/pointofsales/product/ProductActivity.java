package com.ahmadabuhasan.pointofsales.product;

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
import com.ahmadabuhasan.pointofsales.databinding.ActivityProductBinding;
import com.ahmadabuhasan.pointofsales.utils.BaseActivity;
import com.ajts.androidmads.library.SQLiteToExcel;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.obsez.android.lib.filechooser.ChooserDialog;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

/*
 * Created by Ahmad Abu Hasan (C) 2022
 */

public class ProductActivity extends BaseActivity {

    private ActivityProductBinding binding;

    ProgressDialog dialog;
    DatabaseAccess databaseAccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        MobileAds.initialize(this);
        binding.adViewProduct.loadAd(new AdRequest.Builder().build());

        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.all_product);

        this.binding.productRecyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        this.binding.productRecyclerview.setHasFixedSize(true);

        databaseAccess = DatabaseAccess.getInstance(this);
        databaseAccess.open();
        List<HashMap<String, String>> productData = databaseAccess.getProducts();
        Log.d("data", "" + productData.size());
        if (productData.size() <= 0) {
            Toasty.info(this, R.string.no_product_found, Toasty.LENGTH_SHORT).show();
            this.binding.ivNoProduct.setImageResource(R.drawable.no_data);
        } else {
            this.binding.ivNoProduct.setVisibility(View.GONE);
            ProductAdapter adapter = new ProductAdapter(this, productData);
            this.binding.productRecyclerview.setAdapter(adapter);
        }

        this.binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                databaseAccess.open();
                List<HashMap<String, String>> searchProductList = databaseAccess.getSearchProducts(charSequence.toString());
                if (searchProductList.size() <= 0) {
                    binding.productRecyclerview.setVisibility(View.GONE);
                    binding.ivNoProduct.setVisibility(View.VISIBLE);
                    binding.ivNoProduct.setImageResource(R.drawable.no_data);
                } else {
                    binding.ivNoProduct.setVisibility(View.GONE);
                    binding.productRecyclerview.setVisibility(View.VISIBLE);

                    ProductAdapter adapter1 = new ProductAdapter(ProductActivity.this, searchProductList);
                    binding.productRecyclerview.setAdapter(adapter1);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        this.binding.fabAdd.setOnClickListener(view -> this.startActivity(new Intent(ProductActivity.this, AddProductActivity.class)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.all_product_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if (item.getItemId() == R.id.menu_export) {
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
                        ProductActivity.this.onExport(dir);
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
        sqLiteToExcel.exportSingleTable(Constant.products, "products.xls", new SQLiteToExcel.ExportListener() {
            @Override
            public void onStart() {
                ProductActivity.this.dialog = new ProgressDialog(ProductActivity.this);
                ProductActivity.this.dialog.setMessage(ProductActivity.this.getString(R.string.data_exporting_please_wait));
                ProductActivity.this.dialog.setCancelable(false);
                ProductActivity.this.dialog.show();
            }

            @Override
            public void onCompleted(String filePath) {
                Handler mHand = new Handler();
                mHand.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ProductActivity.this.dialog.dismiss();
                        Toasty.success(ProductActivity.this, R.string.data_successfully_exported, Toasty.LENGTH_SHORT).show();
                    }
                }, 5000L);
            }

            @Override
            public void onError(Exception e) {
                ProductActivity.this.dialog.dismiss();
                Toasty.error(ProductActivity.this, R.string.data_export_fail, Toasty.LENGTH_SHORT).show();
            }
        });
    }
}