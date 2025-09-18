package com.ahmadabuhasan.pointofsales.settings.payment_method;

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
import com.ahmadabuhasan.pointofsales.databinding.ActivityPaymentMethodBinding;
import com.ahmadabuhasan.pointofsales.utils.BaseActivity;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

/*
 * Created by Ahmad Abu Hasan (C) 2022
 */

public class PaymentMethodActivity extends BaseActivity {

    private ActivityPaymentMethodBinding binding;
    DatabaseAccess databaseAccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPaymentMethodBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.all_payment_method);

        this.binding.paymentMethodRecyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        this.binding.paymentMethodRecyclerview.setHasFixedSize(true);

        databaseAccess = DatabaseAccess.getInstance(this);
        databaseAccess.open();
        List<HashMap<String, String>> paymentMethodData = databaseAccess.getPaymentMethod();
        Log.d("data", "" + paymentMethodData.size());
        if (paymentMethodData.size() <= 0) {
            Toasty.info(this, R.string.no_data_found, Toasty.LENGTH_SHORT).show();
            this.binding.ivNoPaymentMethod.setImageResource(R.drawable.no_data);
        } else {
            this.binding.ivNoPaymentMethod.setVisibility(View.GONE);
            PaymentMethodAdapter adapter = new PaymentMethodAdapter(this, paymentMethodData);
            this.binding.paymentMethodRecyclerview.setAdapter(adapter);
        }

        this.binding.etPaymentMethodSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                databaseAccess.open();
                List<HashMap<String, String>> searchPaymentMethodList = databaseAccess.searchPaymentMethod(charSequence.toString());
                if (searchPaymentMethodList.size() <= 0) {
                    binding.paymentMethodRecyclerview.setVisibility(View.GONE);
                    binding.ivNoPaymentMethod.setVisibility(View.VISIBLE);
                    binding.ivNoPaymentMethod.setImageResource(R.drawable.no_data);
                } else {
                    binding.paymentMethodRecyclerview.setVisibility(View.VISIBLE);
                    binding.ivNoPaymentMethod.setVisibility(View.GONE);
                    PaymentMethodAdapter adapter = new PaymentMethodAdapter(PaymentMethodActivity.this, searchPaymentMethodList);
                    binding.paymentMethodRecyclerview.setAdapter(adapter);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        this.binding.fabAdd.setOnClickListener(view -> this.startActivity(new Intent(PaymentMethodActivity.this, AddPaymentMethodActivity.class)));
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