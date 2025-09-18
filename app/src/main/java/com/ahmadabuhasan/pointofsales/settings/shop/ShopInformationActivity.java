package com.ahmadabuhasan.pointofsales.settings.shop;

import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.internal.view.SupportMenu;

import com.ahmadabuhasan.pointofsales.Constant;
import com.ahmadabuhasan.pointofsales.R;
import com.ahmadabuhasan.pointofsales.database.DatabaseAccess;
import com.ahmadabuhasan.pointofsales.databinding.ActivityShopInformationBinding;
import com.ahmadabuhasan.pointofsales.utils.BaseActivity;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

/*
 * Created by Ahmad Abu Hasan (C) 2022
 */

public class ShopInformationActivity extends BaseActivity {

    private ActivityShopInformationBinding binding;
    DatabaseAccess databaseAccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShopInformationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.shop_information);

        databaseAccess = DatabaseAccess.getInstance(this);
        databaseAccess.open();
        List<HashMap<String, String>> shopData = databaseAccess.getShopInformation();

        this.binding.etShopName.setText(shopData.get(0).get(Constant.SHOP_NAME));
        this.binding.etShopContact.setText(shopData.get(0).get(Constant.SHOP_CONTACT));
        this.binding.etShopEmail.setText(shopData.get(0).get(Constant.SHOP_EMAIL));
        this.binding.etShopAddress.setText(shopData.get(0).get(Constant.SHOP_ADDRESS));
        this.binding.etShopCurrency.setText(shopData.get(0).get(Constant.SHOP_CURRENCY));
        this.binding.etTax.setText(shopData.get(0).get(Constant.TAX));

        this.binding.etShopName.setEnabled(false);
        this.binding.etShopContact.setEnabled(false);
        this.binding.etShopEmail.setEnabled(false);
        this.binding.etShopAddress.setEnabled(false);
        this.binding.etShopCurrency.setEnabled(false);
        this.binding.etTax.setEnabled(false);

        this.binding.tvShopUpdate.setVisibility(View.GONE);
        this.binding.tvShopEdit.setOnClickListener(view -> {
            this.binding.etShopName.setEnabled(true);
            this.binding.etShopContact.setEnabled(true);
            this.binding.etShopEmail.setEnabled(true);
            this.binding.etShopAddress.setEnabled(true);
            this.binding.etShopCurrency.setEnabled(true);
            this.binding.etTax.setEnabled(true);

            this.binding.etShopName.setTextColor(SupportMenu.CATEGORY_MASK);
            this.binding.etShopContact.setTextColor(SupportMenu.CATEGORY_MASK);
            this.binding.etShopEmail.setTextColor(SupportMenu.CATEGORY_MASK);
            this.binding.etShopAddress.setTextColor(SupportMenu.CATEGORY_MASK);
            this.binding.etShopCurrency.setTextColor(SupportMenu.CATEGORY_MASK);
            this.binding.etTax.setTextColor(SupportMenu.CATEGORY_MASK);

            this.binding.tvShopUpdate.setVisibility(View.VISIBLE);
            this.binding.tvShopEdit.setVisibility(View.GONE);
        });

        this.binding.tvShopUpdate.setOnClickListener(view -> {
            String shop_name = this.binding.etShopName.getText().toString().trim();
            String shop_contact = this.binding.etShopContact.getText().toString().trim();
            String shop_email = this.binding.etShopEmail.getText().toString().trim();
            String shop_address = this.binding.etShopAddress.getText().toString().trim();
            String shop_currency = this.binding.etShopCurrency.getText().toString().trim();
            String tax = this.binding.etTax.getText().toString().trim();

            if (shop_name.isEmpty()) {
                this.binding.etShopName.setError(this.getString(R.string.shop_name_cannot_be_empty));
                this.binding.etShopName.requestFocus();
            } else if (shop_contact.isEmpty()) {
                this.binding.etShopContact.setError(this.getString(R.string.shop_contact_cannot_be_empty));
                this.binding.etShopContact.requestFocus();
            } else if (shop_email.isEmpty() || !shop_email.contains("@") || !shop_email.contains(".")) {
                this.binding.etShopEmail.setError(this.getString(R.string.enter_valid_email));
                this.binding.etShopEmail.requestFocus();
            } else if (shop_address.isEmpty()) {
                this.binding.etShopAddress.setError(this.getString(R.string.shop_address_cannot_be_empty));
                this.binding.etShopAddress.requestFocus();
            } else if (shop_currency.isEmpty()) {
                this.binding.etShopCurrency.setError(this.getString(R.string.shop_currency_cannot_be_empty));
                this.binding.etShopCurrency.requestFocus();
            } else if (tax.isEmpty()) {
                this.binding.etTax.setError(this.getString(R.string.tax_in_percentage));
                this.binding.etTax.requestFocus();
            } else {
                Toasty.warning(this, Html.fromHtml("<small>Please purchase for</small><br> <big><b>Point Of Sale PRO</b></big>.<br> Thank you"), Toasty.LENGTH_LONG).show();

                /*databaseAccess.open();
                boolean check = databaseAccess.updateShopInformation(shop_name, shop_contact, shop_email, shop_address, shop_currency, tax);
                if (check) {
                    Toasty.success(this, R.string.shop_information_updated_successfully, Toasty.LENGTH_SHORT).show();
                    Intent i = new Intent(ShopInformationActivity.this, SettingsActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    this.startActivity(i);
                } else {
                    Toasty.error(this, R.string.failed, Toasty.LENGTH_SHORT).show();
                }*/
            }
        });
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