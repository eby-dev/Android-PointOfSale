package com.ahmadabuhasan.pointofsales.settings.order_type;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.internal.view.SupportMenu;

import com.ahmadabuhasan.pointofsales.Constant;
import com.ahmadabuhasan.pointofsales.R;
import com.ahmadabuhasan.pointofsales.database.DatabaseAccess;
import com.ahmadabuhasan.pointofsales.databinding.ActivityEditDeliveryBinding;
import com.ahmadabuhasan.pointofsales.utils.BaseActivity;

import java.util.Objects;

import es.dmoral.toasty.Toasty;

/*
 * Created by Ahmad Abu Hasan (C) 2022
 */

public class EditDeliveryActivity extends BaseActivity {

    private ActivityEditDeliveryBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditDeliveryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edit Delivery");

        final String delivery_id = getIntent().getExtras().getString(Constant.ORDER_TYPE_ID);

        this.binding.etDeliveryName.setText(getIntent().getExtras().getString(Constant.ORDER_TYPE_NAME));
        this.binding.etDeliveryName.setEnabled(false);

        this.binding.tvUpdateDelivery.setVisibility(View.GONE);
        this.binding.tvEditDelivery.setOnClickListener(view -> {
            this.binding.etDeliveryName.setEnabled(true);
            this.binding.etDeliveryName.setTextColor(SupportMenu.CATEGORY_MASK);
            this.binding.tvEditDelivery.setVisibility(View.GONE);
            this.binding.tvUpdateDelivery.setVisibility(View.VISIBLE);
        });

        this.binding.tvUpdateDelivery.setOnClickListener(view -> {
            String delivery_name = this.binding.etDeliveryName.getText().toString().trim();
            if (delivery_name.isEmpty()) {
                this.binding.etDeliveryName.setError(this.getString(R.string.enter_delivery_name));
                this.binding.etDeliveryName.requestFocus();
            } else {
                DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);

                databaseAccess.open();
                if (databaseAccess.updateOrderType(delivery_id, delivery_name)) {
                    Toasty.success(this, R.string.update_successfully, Toasty.LENGTH_SHORT).show();
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