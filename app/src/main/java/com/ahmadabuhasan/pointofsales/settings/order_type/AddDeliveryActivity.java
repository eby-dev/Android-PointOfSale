package com.ahmadabuhasan.pointofsales.settings.order_type;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.ahmadabuhasan.pointofsales.R;
import com.ahmadabuhasan.pointofsales.database.DatabaseAccess;
import com.ahmadabuhasan.pointofsales.databinding.ActivityAddDeliveryBinding;
import com.ahmadabuhasan.pointofsales.utils.BaseActivity;

import java.util.Objects;

import es.dmoral.toasty.Toasty;

/*
 * Created by Ahmad Abu Hasan (C) 2022
 */

public class AddDeliveryActivity extends BaseActivity {

    private ActivityAddDeliveryBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddDeliveryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add Delivery");

        this.binding.tvAddDelivery.setOnClickListener(view -> {
            String delivery_name = this.binding.etDeliveryName.getText().toString().trim();
            if (delivery_name.isEmpty()) {
                this.binding.etDeliveryName.setError(this.getString(R.string.enter_delivery_name));
                this.binding.etDeliveryName.requestFocus();
            } else {
                DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);

                databaseAccess.open();
                if (databaseAccess.addOrderType(delivery_name)) {
                    Toasty.success(this, R.string.successfully_added, Toasty.LENGTH_SHORT).show();
                    Intent i = new Intent(this, DeliveryActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    this.startActivity(i);
                } else {
                    Toasty.error(this, R.string.failed, Toasty.LENGTH_SHORT).show();
                }
            }
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