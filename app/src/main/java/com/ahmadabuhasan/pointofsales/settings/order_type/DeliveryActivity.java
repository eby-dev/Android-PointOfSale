package com.ahmadabuhasan.pointofsales.settings.order_type;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ahmadabuhasan.pointofsales.R;
import com.ahmadabuhasan.pointofsales.database.DatabaseAccess;
import com.ahmadabuhasan.pointofsales.databinding.ActivityDeliveryBinding;
import com.ahmadabuhasan.pointofsales.utils.BaseActivity;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

/*
 * Created by Ahmad Abu Hasan (C) 2022
 */

public class DeliveryActivity extends BaseActivity {

    private ActivityDeliveryBinding binding;
    DatabaseAccess databaseAccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDeliveryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("All Delivery");

        this.binding.deliveryRecyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        this.binding.deliveryRecyclerview.setHasFixedSize(true);

        databaseAccess = DatabaseAccess.getInstance(this);
        databaseAccess.open();
        List<HashMap<String, String>> deliveryData = databaseAccess.getOrderType();
        Log.d("data", "" + deliveryData.size());
        if (deliveryData.size() <= 0) {
            Toasty.info(this, R.string.no_data_found, Toasty.LENGTH_SHORT).show();
            this.binding.ivNoDelivery.setImageResource(R.drawable.no_data);
        } else {
            this.binding.ivNoDelivery.setVisibility(View.GONE);
            this.binding.deliveryRecyclerview.setAdapter(new DeliveryAdapter(this, deliveryData));
        }

        this.binding.etDeliverySearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                databaseAccess.open();
                List<HashMap<String, String>> searchDeliveryList = databaseAccess.searchOrderType(charSequence.toString());
                if (searchDeliveryList.size() <= 0) {
                    binding.deliveryRecyclerview.setVisibility(View.GONE);
                    binding.ivNoDelivery.setVisibility(View.VISIBLE);
                    binding.ivNoDelivery.setImageResource(R.drawable.no_data);
                } else {
                    binding.ivNoDelivery.setVisibility(View.GONE);
                    binding.deliveryRecyclerview.setVisibility(View.VISIBLE);
                    binding.deliveryRecyclerview.setAdapter(new DeliveryAdapter(DeliveryActivity.this, searchDeliveryList));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        this.binding.fabAdd.setOnClickListener(view -> this.startActivity(new Intent(DeliveryActivity.this, AddDeliveryActivity.class)));
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