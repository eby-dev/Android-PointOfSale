package com.ahmadabuhasan.pointofsales.suppliers;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.internal.view.SupportMenu;

import com.ahmadabuhasan.pointofsales.Constant;
import com.ahmadabuhasan.pointofsales.R;
import com.ahmadabuhasan.pointofsales.database.DatabaseAccess;
import com.ahmadabuhasan.pointofsales.databinding.ActivityEditSuppliersBinding;
import com.ahmadabuhasan.pointofsales.utils.BaseActivity;

import java.util.Objects;

import es.dmoral.toasty.Toasty;

/*
 * Created by Ahmad Abu Hasan (C) 2022
 */

public class EditSuppliersActivity extends BaseActivity {

    private ActivityEditSuppliersBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditSuppliersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.edit_suppliers);

        final String getSupplier_id = getIntent().getExtras().getString(Constant.SUPPLIERS_ID);

        this.binding.etSupplierName.setText(getIntent().getExtras().getString(Constant.SUPPLIERS_NAME));
        this.binding.etSupplierContactName.setText(getIntent().getExtras().getString(Constant.SUPPLIERS_CONTACT_PERSON));
        this.binding.etSupplierCell.setText(getIntent().getExtras().getString(Constant.SUPPLIERS_CELL));
        this.binding.etSupplierEmail.setText(getIntent().getExtras().getString(Constant.SUPPLIERS_EMAIL));
        this.binding.etSupplierAddress.setText(getIntent().getExtras().getString(Constant.SUPPLIERS_ADDRESS));

        this.binding.etSupplierName.setEnabled(false);
        this.binding.etSupplierContactName.setEnabled(false);
        this.binding.etSupplierCell.setEnabled(false);
        this.binding.etSupplierEmail.setEnabled(false);
        this.binding.etSupplierAddress.setEnabled(false);

        this.binding.tvUpdateSupplier.setVisibility(View.GONE);
        this.binding.tvEditSupplier.setOnClickListener(view -> {
            this.binding.etSupplierName.setEnabled(true);
            this.binding.etSupplierContactName.setEnabled(true);
            this.binding.etSupplierCell.setEnabled(true);
            this.binding.etSupplierEmail.setEnabled(true);
            this.binding.etSupplierAddress.setEnabled(true);

            this.binding.etSupplierName.setTextColor(SupportMenu.CATEGORY_MASK);
            this.binding.etSupplierContactName.setTextColor(SupportMenu.CATEGORY_MASK);
            this.binding.etSupplierCell.setTextColor(SupportMenu.CATEGORY_MASK);
            this.binding.etSupplierEmail.setTextColor(SupportMenu.CATEGORY_MASK);
            this.binding.etSupplierAddress.setTextColor(SupportMenu.CATEGORY_MASK);

            this.binding.tvEditSupplier.setVisibility(View.GONE);
            this.binding.tvUpdateSupplier.setVisibility(View.VISIBLE);
        });

        this.binding.tvUpdateSupplier.setOnClickListener(view -> {
            String supplier_name = this.binding.etSupplierName.getText().toString().trim();
            String supplier_contact_name = this.binding.etSupplierContactName.getText().toString().trim();
            String supplier_cell = this.binding.etSupplierCell.getText().toString().trim();
            String supplier_email = this.binding.etSupplierEmail.getText().toString().trim();
            String supplier_address = this.binding.etSupplierAddress.getText().toString().trim();

            if (supplier_name.isEmpty()) {
                this.binding.etSupplierName.setError(this.getString(R.string.enter_suppliers_name));
                this.binding.etSupplierName.requestFocus();
            } else if (supplier_contact_name.isEmpty()) {
                this.binding.etSupplierContactName.setError(this.getString(R.string.enter_suppliers_contact_person_name));
                this.binding.etSupplierContactName.requestFocus();
            } else if (supplier_cell.isEmpty()) {
                this.binding.etSupplierCell.setError(this.getString(R.string.enter_suppliers_cell));
                this.binding.etSupplierCell.requestFocus();
            } else if (supplier_email.isEmpty() || !supplier_email.contains("@") || !supplier_email.contains(".")) {
                this.binding.etSupplierEmail.setError(this.getString(R.string.enter_valid_email));
                this.binding.etSupplierEmail.requestFocus();
            } else if (supplier_address.isEmpty()) {
                this.binding.etSupplierAddress.setError(this.getString(R.string.enter_suppliers_address));
                this.binding.etSupplierAddress.requestFocus();
            } else {
                DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);

                databaseAccess.open();
                if (databaseAccess.updateSuppliers(getSupplier_id, supplier_name, supplier_contact_name, supplier_cell, supplier_email, supplier_address)) {
                    Toasty.success(this, R.string.update_successfully, Toasty.LENGTH_SHORT).show();
                    Intent i = new Intent(EditSuppliersActivity.this, SuppliersActivity.class);
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