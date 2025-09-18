package com.ahmadabuhasan.pointofsales.orders;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmadabuhasan.pointofsales.DashboardActivity;
import com.ahmadabuhasan.pointofsales.R;
import com.ahmadabuhasan.pointofsales.database.DatabaseAccess;
import com.ahmadabuhasan.pointofsales.databinding.ActivityOrdersBinding;
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

public class OrdersActivity extends BaseActivity {

    private ActivityOrdersBinding binding;
    DatabaseAccess databaseAccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrdersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        MobileAds.initialize(this);
        binding.adViewOrders.loadAd(new AdRequest.Builder().build());

        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.order_history);

        this.binding.ivNoOrder.setVisibility(View.GONE);
        this.binding.tvNoOrder.setVisibility(View.GONE);

        this.binding.ordersRecyclerview.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        this.binding.ordersRecyclerview.setHasFixedSize(true);

        databaseAccess = DatabaseAccess.getInstance(this);
        databaseAccess.open();
        List<HashMap<String, String>> orderList = databaseAccess.getOrderList();
        if (orderList.size() <= 0) {
            Toasty.info(this, R.string.no_order_found, Toasty.LENGTH_SHORT).show();
            this.binding.ordersRecyclerview.setVisibility(View.GONE);
            this.binding.tvNoOrder.setVisibility(View.VISIBLE);
            this.binding.ivNoOrder.setVisibility(View.VISIBLE);
            this.binding.ivNoOrder.setImageResource(R.drawable.not_found);
        } else {
            OrderAdapter adapter = new OrderAdapter(OrdersActivity.this, orderList);
            this.binding.ordersRecyclerview.setAdapter(adapter);
        }

        this.binding.etSearchOrder.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                databaseAccess.open();
                List<HashMap<String, String>> searchOrder = databaseAccess.searchOrderList(charSequence.toString());
                if (searchOrder.size() <= 0) {
                    binding.ordersRecyclerview.setVisibility(View.GONE);
                    binding.ivNoOrder.setVisibility(View.VISIBLE);
                    binding.ivNoOrder.setImageResource(R.drawable.no_data);
                    return;
                }
                binding.ivNoOrder.setVisibility(View.GONE);
                binding.ordersRecyclerview.setVisibility(View.VISIBLE);

                OrderAdapter adapter1 = new OrderAdapter(OrdersActivity.this, searchOrder);
                binding.ordersRecyclerview.setAdapter(adapter1);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, DashboardActivity.class));
        finish();
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            startActivity(new Intent(this, DashboardActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}