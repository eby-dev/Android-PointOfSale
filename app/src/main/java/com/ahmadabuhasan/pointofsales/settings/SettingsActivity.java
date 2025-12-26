package com.ahmadabuhasan.pointofsales.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.ahmadabuhasan.pointofsales.R;
import com.ahmadabuhasan.pointofsales.database.DatabaseAccess;
import com.ahmadabuhasan.pointofsales.databinding.ActivitySettingsBinding;
import com.ahmadabuhasan.pointofsales.settings.backup.BackupActivity;
import com.ahmadabuhasan.pointofsales.settings.categories.CategoriesActivity;
import com.ahmadabuhasan.pointofsales.settings.order_type.DeliveryActivity;
import com.ahmadabuhasan.pointofsales.settings.payment_method.PaymentMethodActivity;
import com.ahmadabuhasan.pointofsales.settings.shop.ShopInformationActivity;
import com.ahmadabuhasan.pointofsales.settings.weight.WeightActivity;
import com.ahmadabuhasan.pointofsales.utils.BaseActivity;
import com.ahmadabuhasan.pointofsales.utils.Utils;

import java.util.Objects;

import es.dmoral.toasty.Toasty;

/*
 * Created by Ahmad Abu Hasan (C) 2022
 */

public class SettingsActivity extends BaseActivity {

    private ActivitySettingsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.action_settings);

        new Utils().interstitialAdsShow(this);

        this.binding.cvShopInfo.setOnClickListener(view -> this.startActivity(new Intent(SettingsActivity.this, ShopInformationActivity.class)));
        this.binding.cvCategory.setOnClickListener(view -> this.startActivity(new Intent(SettingsActivity.this, CategoriesActivity.class)));
        this.binding.cvWeight.setOnClickListener(view -> this.startActivity(new Intent(SettingsActivity.this, WeightActivity.class)));
        this.binding.cvDelivery.setOnClickListener(view -> this.startActivity(new Intent(SettingsActivity.this, DeliveryActivity.class)));
        this.binding.cvPaymentMethod.setOnClickListener(view -> this.startActivity(new Intent(SettingsActivity.this, PaymentMethodActivity.class)));
        this.binding.cvBackup.setOnClickListener(view -> this.startActivity(new Intent(SettingsActivity.this, BackupActivity.class)));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        } else if (item.getItemId() == R.id.menu_reset) {
            final DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);
            databaseAccess.open();
            new AlertDialog.Builder(this)
                    .setCancelable(false)
                    .setTitle(getString(R.string.confirmation))
                    .setMessage(getString(R.string.are_you_sure_you_want_to_delete_all_data))
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.yes), (dialog, which) -> {
                        databaseAccess.clearAllData();
                        Toasty.success(this, R.string.all_data_deleted_successfully, Toasty.LENGTH_SHORT).show();
                        dialog.dismiss();
                    })
                    .setNegativeButton(getString(R.string.no), (dialog, which) -> {
                        databaseAccess.close();
                        dialog.dismiss();
                    })
                    .show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.setting_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
}