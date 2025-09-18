package com.ahmadabuhasan.pointofsales.settings.payment_method;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.ahmadabuhasan.pointofsales.R;
import com.ahmadabuhasan.pointofsales.database.DatabaseAccess;
import com.ahmadabuhasan.pointofsales.databinding.ActivityAddPaymentMethodBinding;
import com.ahmadabuhasan.pointofsales.utils.BaseActivity;

import java.util.Objects;

import es.dmoral.toasty.Toasty;

/*
 * Created by Ahmad Abu Hasan (C) 2022
 */

public class AddPaymentMethodActivity extends BaseActivity {

    private ActivityAddPaymentMethodBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddPaymentMethodBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.add_payment_method);

        this.binding.tvAddPaymentMethod.setOnClickListener(view -> {
            String payment_method_name = this.binding.etPaymentMethodName.getText().toString().trim();
            if (payment_method_name.isEmpty()) {
                this.binding.etPaymentMethodName.setError(this.getString(R.string.enter_payment_method_name));
                this.binding.etPaymentMethodName.requestFocus();
            } else {
                DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);

                databaseAccess.open();
                if (databaseAccess.addPaymentMethod(payment_method_name)) {
                    Toasty.success(this, R.string.successfully_added, Toasty.LENGTH_SHORT).show();
                    Intent i = new Intent(AddPaymentMethodActivity.this, PaymentMethodActivity.class);
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