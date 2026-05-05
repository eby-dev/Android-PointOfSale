package com.ahmadabuhasan.pointofsales.customers;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.ahmadabuhasan.pointofsales.DashboardActivity;
import com.ahmadabuhasan.pointofsales.R;
import com.ahmadabuhasan.pointofsales.database.DatabaseAccess;
import com.ahmadabuhasan.pointofsales.database.DatabaseOpenHelper;
import com.ahmadabuhasan.pointofsales.databinding.ActivityAddCustomersBinding;
import com.ahmadabuhasan.pointofsales.utils.BaseActivity;
import com.ajts.androidmads.library.ExcelToSQLite;
import com.obsez.android.lib.filechooser.ChooserDialog;

import java.io.File;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

/*
 * Created by Ahmad Abu Hasan (C) 2022
 */

public class AddCustomersActivity extends BaseActivity {

    private ActivityAddCustomersBinding binding;
    ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddCustomersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.add_customer);

        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);

        this.binding.tvAddCustomer.setOnClickListener(view -> {
            String customer_name = this.binding.etCustomerName.getText().toString().trim();
            String customer_cell = this.binding.etCustomerCell.getText().toString().trim();
            String customer_email = this.binding.etCustomerEmail.getText().toString().trim();
            String customer_address = this.binding.etCustomerAddress.getText().toString().trim();

            if (customer_name.isEmpty()) {
                this.binding.etCustomerName.setError(getString(R.string.enter_customer_name));
                this.binding.etCustomerName.requestFocus();
            } else if (customer_cell.isEmpty()) {
                this.binding.etCustomerCell.setError(getString(R.string.enter_customer_cell));
                this.binding.etCustomerCell.requestFocus();
            } else if (!customer_email.contains("@") || !customer_email.contains(".")) {
                this.binding.etCustomerEmail.setError(getString(R.string.enter_valid_email));
                this.binding.etCustomerEmail.requestFocus();
            } else if (customer_address.isEmpty()) {
                this.binding.etCustomerAddress.setError(getString(R.string.enter_customer_address));
                this.binding.etCustomerAddress.requestFocus();
            } else {
                databaseAccess.open();
                if (databaseAccess.addCustomer(customer_name, customer_cell, customer_email, customer_address)) {
                    Toasty.success(this, R.string.customer_successfully_added, Toasty.LENGTH_SHORT).show();
                    Intent i = new Intent(AddCustomersActivity.this, CustomersActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    this.startActivity(i);
                } else {
                    Toasty.error(this, R.string.failed, Toasty.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_product_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if (item.getItemId() == R.id.menu_import) {
            fileChooser();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    public void fileChooser() {
        new ChooserDialog(this)
                .displayPath(true)
                .withFilter(false, false, "xls")
                .withChosenListener((dir, dirFile) -> AddCustomersActivity.this.onImport(dir)).withOnCancelListener(dialogInterface -> {
                    dialogInterface.cancel();
                    Log.d("CANCEL", "CANCEL");
                }).build().show();
    }

    public void onImport(String path) {
        DatabaseAccess databaseAccess1 = DatabaseAccess.getInstance(this);
        databaseAccess1.open();
        File file = new File(path);
        if (!file.exists()) {
            Toast.makeText(this, R.string.no_file_found, Toast.LENGTH_SHORT).show();
            return;
        }
        ExcelToSQLite excelToSQLite = new ExcelToSQLite(getApplicationContext(), DatabaseOpenHelper.DATABASE_NAME, false);
        excelToSQLite.importFromFile(path, new ExcelToSQLite.ImportListener() {
            @Override
            public void onStart() {
                AddCustomersActivity.this.loading = new ProgressDialog(AddCustomersActivity.this);
                AddCustomersActivity.this.loading.setMessage(AddCustomersActivity.this.getString(R.string.data_importing_please_wait));
                AddCustomersActivity.this.loading.setCancelable(false);
                AddCustomersActivity.this.loading.show();
            }

            @Override
            public void onCompleted(String dbName) {
                Handler mHand = new Handler();
                mHand.postDelayed(() -> {
                    AddCustomersActivity.this.loading.dismiss();
                    Toasty.success(AddCustomersActivity.this, R.string.data_successfully_imported, Toasty.LENGTH_SHORT).show();
                    Intent i = new Intent(AddCustomersActivity.this, DashboardActivity.class);
                    AddCustomersActivity.this.startActivity(i);
                    AddCustomersActivity.this.finish();
                }, 5000L);
            }

            @Override
            public void onError(Exception e) {
                AddCustomersActivity.this.loading.dismiss();
                Toasty.error(AddCustomersActivity.this, R.string.data_import_fail, Toasty.LENGTH_SHORT).show();
                Log.d("Error : ", "" + e.getMessage());
            }
        });
    }
}