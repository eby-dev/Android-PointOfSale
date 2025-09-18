package com.ahmadabuhasan.pointofsales.settings.payment_method;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.internal.view.SupportMenu;

import com.ahmadabuhasan.pointofsales.Constant;
import com.ahmadabuhasan.pointofsales.R;
import com.ahmadabuhasan.pointofsales.database.DatabaseAccess;
import com.ahmadabuhasan.pointofsales.databinding.ActivityEditPaymentMethodBinding;
import com.ahmadabuhasan.pointofsales.utils.BaseActivity;

import java.util.Objects;

import es.dmoral.toasty.Toasty;

/*
 * Created by Ahmad Abu Hasan (C) 2022
 */

public class EditPaymentMethodActivity extends BaseActivity {

    private ActivityEditPaymentMethodBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditPaymentMethodBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.update_payment_method);

        final String payment_method_id = getIntent().getExtras().getString(Constant.PAYMENT_METHOD_ID);

        this.binding.etPaymentMethodName.setText(getIntent().getExtras().getString(Constant.PAYMENT_METHOD_NAME));
        this.binding.etPaymentMethodName.setEnabled(false);

        this.binding.tvUpdatePaymentMethod.setVisibility(View.GONE);
        this.binding.tvEditPaymentMethod.setOnClickListener(view -> {
            this.binding.etPaymentMethodName.setEnabled(true);
            this.binding.etPaymentMethodName.setTextColor(SupportMenu.CATEGORY_MASK);
            this.binding.tvEditPaymentMethod.setVisibility(View.GONE);
            this.binding.tvUpdatePaymentMethod.setVisibility(View.VISIBLE);
        });

        this.binding.tvUpdatePaymentMethod.setOnClickListener(view -> {
            String payment_method_name = this.binding.etPaymentMethodName.getText().toString().trim();
            if (payment_method_name.isEmpty()) {
                this.binding.etPaymentMethodName.setError(this.getString(R.string.payment_method_name));
                this.binding.etPaymentMethodName.requestFocus();
            } else {
                DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);

                databaseAccess.open();
                if (databaseAccess.updatePaymentMethod(payment_method_id, payment_method_name)) {
                    Toasty.success(this, R.string.successfully_added, Toasty.LENGTH_SHORT).show();
                    Intent i = new Intent(EditPaymentMethodActivity.this, PaymentMethodActivity.class);
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