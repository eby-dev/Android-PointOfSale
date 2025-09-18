package com.ahmadabuhasan.pointofsales.suppliers;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import com.ahmadabuhasan.pointofsales.databinding.ActivityAddSuppliersBinding;
import com.ahmadabuhasan.pointofsales.utils.BaseActivity;
import com.ajts.androidmads.library.ExcelToSQLite;
import com.obsez.android.lib.filechooser.ChooserDialog;

import java.io.File;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

/*
 * Created by Ahmad Abu Hasan (C) 2022
 */

public class AddSuppliersActivity extends BaseActivity {

    private ActivityAddSuppliersBinding binding;

    ProgressDialog loading;
    DatabaseAccess databaseAccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddSuppliersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.add_supplier);

        databaseAccess = DatabaseAccess.getInstance(this);

        this.binding.tvAddSupplier.setOnClickListener(view -> {
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
                databaseAccess.open();
                if (databaseAccess.addSuppliers(supplier_name, supplier_contact_name, supplier_cell, supplier_email, supplier_address)) {
                    Toasty.success(this, R.string.suppliers_successfully_added, Toasty.LENGTH_SHORT).show();
                    Intent i = new Intent(AddSuppliersActivity.this, SuppliersActivity.class);
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
        getMenuInflater().inflate(R.menu.add_supplier_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if (item.getItemId() == R.id.menu_import_supplier) {
            fileChooser();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    public void fileChooser() {
        new ChooserDialog((Activity) this)
                .displayPath(true)
                .withFilter(false, false, "xls")
                .withChosenListener(new ChooserDialog.Result() {
                    @Override
                    public void onChoosePath(String dir, File dirFile) {
                        AddSuppliersActivity.this.onImport(dir);
                    }
                }).withOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                Log.d("CANCEL", "CANCEL");
                dialogInterface.cancel();
            }
        }).build().show();
    }

    public void onImport(String path) {
        databaseAccess.open();
        File file = new File(path);
        if (!file.exists()) {
            Toast.makeText(this, R.string.no_file_found, Toast.LENGTH_SHORT).show();
            return;
        }
        ExcelToSQLite excelToSQLite = new ExcelToSQLite(getApplicationContext(), DatabaseOpenHelper.DATABASE_NAME, false);
        excelToSQLite.importFromFile(path, new ExcelToSQLite.ImportListener() {
            @Override
            public void onStart() {
                AddSuppliersActivity.this.loading = new ProgressDialog(AddSuppliersActivity.this);
                AddSuppliersActivity.this.loading.setMessage(AddSuppliersActivity.this.getString(R.string.data_importing_please_wait));
                AddSuppliersActivity.this.loading.setCancelable(false);
                AddSuppliersActivity.this.loading.show();
            }

            @Override
            public void onCompleted(String dbName) {
                Handler mHand = new Handler();
                mHand.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        AddSuppliersActivity.this.loading.dismiss();
                        Toasty.success(AddSuppliersActivity.this, R.string.data_successfully_imported, Toasty.LENGTH_SHORT).show();
                        Intent i = new Intent(AddSuppliersActivity.this, DashboardActivity.class);
                        AddSuppliersActivity.this.startActivity(i);
                        AddSuppliersActivity.this.finish();
                    }
                }, 5000L);
            }

            @Override
            public void onError(Exception e) {
                AddSuppliersActivity.this.loading.dismiss();
                Toasty.error(AddSuppliersActivity.this, R.string.data_import_fail, Toasty.LENGTH_SHORT).show();
                Log.d("Error : ", "" + e.getMessage());
            }
        });
    }
}