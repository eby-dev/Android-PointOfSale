package com.ahmadabuhasan.pointofsales.pos;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ahmadabuhasan.pointofsales.DashboardActivity;
import com.ahmadabuhasan.pointofsales.R;
import com.ahmadabuhasan.pointofsales.database.DatabaseAccess;
import com.ahmadabuhasan.pointofsales.databinding.ActivityPosBinding;
import com.ahmadabuhasan.pointofsales.utils.BaseActivity;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

/*
 * Created by Ahmad Abu Hasan (C) 2022
 */

public class PosActivity extends BaseActivity {

    public static TextView tvCount;
    public static EditText etSearch;
    private ActivityPosBinding binding;

    int spanCount;
    PosProductAdapter posProductAdapter;
    ProductCategoryAdapter productCategoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPosBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        MobileAds.initialize(this);
        binding.adViewPos.loadAd(new AdRequest.Builder().build());

        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.all_product);
        getSupportActionBar().hide();

        this.binding.ivNoProduct.setVisibility(View.GONE);
        this.binding.tvNoProduct.setVisibility(View.GONE);

        if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE) {
            this.spanCount = Configuration.SCREENLAYOUT_SIZE_XLARGE;
        } else if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_NORMAL) {
            this.spanCount = Configuration.SCREENLAYOUT_SIZE_NORMAL;
        } else if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_SMALL) {
            this.spanCount = Configuration.SCREENLAYOUT_SIZE_NORMAL;
        } else {
            this.spanCount = Configuration.SCREENLAYOUT_SIZE_XLARGE;
        }

        tvCount = binding.tvCount;
        final DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);
        databaseAccess.open();
        int count = databaseAccess.getCartItemCount();
        if (count == 0) {
            tvCount.setVisibility(View.INVISIBLE);
        } else {
            tvCount.setVisibility(View.VISIBLE);
            tvCount.setText(String.valueOf(count));
        }

        this.binding.ivBack.setOnClickListener(view -> {
            startActivity(new Intent(PosActivity.this, DashboardActivity.class));
            finish();
        });
        this.binding.ivCart.setOnClickListener(view -> startActivity(new Intent(PosActivity.this, ProductCartActivity.class)));
        this.binding.ivScanner.setOnClickListener(view -> startActivity(new Intent(PosActivity.this, ScannerActivity.class)));

        this.binding.tvReset.setOnClickListener(view -> {
            etSearch.getText().clear();
            databaseAccess.open();
            List<HashMap<String, String>> productList = databaseAccess.getProducts();
            if (productList.isEmpty()) {
                binding.posRecyclerview.setVisibility(View.GONE);
                binding.tvNoProduct.setVisibility(View.VISIBLE);
                binding.ivNoProduct.setVisibility(View.VISIBLE);
                binding.ivNoProduct.setImageResource(R.drawable.not_found);
            } else {
                binding.tvNoProduct.setVisibility(View.GONE);
                binding.ivNoProduct.setVisibility(View.GONE);
                binding.posRecyclerview.setVisibility(View.VISIBLE);

                posProductAdapter = new PosProductAdapter(PosActivity.this, productList);
                binding.posRecyclerview.setAdapter(posProductAdapter);
            }
        });

        this.binding.categoryRecyclerview.setLayoutManager(new LinearLayoutManager(PosActivity.this, LinearLayoutManager.HORIZONTAL, false));
        this.binding.categoryRecyclerview.setHasFixedSize(true);
        databaseAccess.open();
        List<HashMap<String, String>> categoryData = databaseAccess.getProductCategory();
        Log.d("data", "" + categoryData.size());
        if (categoryData.isEmpty()) {
            Toasty.info(this, R.string.no_data_found, Toasty.LENGTH_SHORT).show();
        } else {
            productCategoryAdapter = new ProductCategoryAdapter(PosActivity.this, categoryData, binding.posRecyclerview, binding.ivNoProduct, binding.tvNoProduct);
            binding.categoryRecyclerview.setAdapter(productCategoryAdapter);
        }

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        this.binding.posRecyclerview.setLayoutManager(gridLayoutManager);
        this.binding.posRecyclerview.setHasFixedSize(true);

        etSearch = binding.etSearch;
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                databaseAccess.open();
                List<HashMap<String, String>> searchProductList = databaseAccess.getSearchProducts(charSequence.toString());
                if (searchProductList.size() <= 0) {
                    binding.posRecyclerview.setVisibility(View.GONE);
                    binding.tvNoProduct.setVisibility(View.VISIBLE);
                    binding.ivNoProduct.setVisibility(View.VISIBLE);
                    binding.ivNoProduct.setImageResource(R.drawable.not_found);
                } else {
                    binding.tvNoProduct.setVisibility(View.GONE);
                    binding.ivNoProduct.setVisibility(View.GONE);
                    binding.posRecyclerview.setVisibility(View.VISIBLE);

                    posProductAdapter = new PosProductAdapter(PosActivity.this, searchProductList);
                    binding.posRecyclerview.setAdapter(posProductAdapter);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        databaseAccess.open();
        List<HashMap<String, String>> productList = databaseAccess.getProducts();
        if (productList.size() <= 0) {
            this.binding.posRecyclerview.setVisibility(View.GONE);
            this.binding.tvNoProduct.setVisibility(View.VISIBLE);
            this.binding.ivNoProduct.setVisibility(View.VISIBLE);
            this.binding.ivNoProduct.setImageResource(R.drawable.not_found);
        } else {
            this.binding.tvNoProduct.setVisibility(View.GONE);
            this.binding.ivNoProduct.setVisibility(View.GONE);
            this.binding.posRecyclerview.setVisibility(View.VISIBLE);

            posProductAdapter = new PosProductAdapter(this, productList);
            this.binding.posRecyclerview.setAdapter(posProductAdapter);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_cart, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if (item.getItemId() == R.id.menu_cart_button) {
            startActivity(new Intent(this, ProductCartActivity.class));
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}