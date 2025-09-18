package com.ahmadabuhasan.pointofsales.product;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.internal.view.SupportMenu;

import com.ahmadabuhasan.pointofsales.Constant;
import com.ahmadabuhasan.pointofsales.R;
import com.ahmadabuhasan.pointofsales.database.DatabaseAccess;
import com.ahmadabuhasan.pointofsales.databinding.ActivityEditProductBinding;
import com.ahmadabuhasan.pointofsales.utils.BaseActivity;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import es.dmoral.toasty.Toasty;
import in.mayanknagwanshi.imagepicker.ImageSelectActivity;

/*
 * Created by Ahmad Abu Hasan (C) 2022
 */

public class EditProductActivity extends BaseActivity {

    @SuppressLint("StaticFieldLeak")
    public static EditText etProductCode;

    private ActivityEditProductBinding binding;

    ArrayAdapter<String> categoryAdapter;
    List<String> categoryNames;
    String selectedCategoryID;

    ArrayAdapter<String> weightUnitAdapter;
    List<String> weightUnitNames;
    String selectedWeightUnitID;

    ArrayAdapter<String> supplierAdapter;
    List<String> supplierNames;
    String selectedSupplierID;

    String productID;
    String mediaPath, encodedImage = "N/A";
    DatabaseAccess databaseAccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.product_details);

        etProductCode = findViewById(R.id.et_product_code);
        this.productID = getIntent().getExtras().getString(Constant.PRODUCT_ID);

        this.binding.etProductName.setEnabled(false);
        etProductCode.setEnabled(false);
        this.binding.ivScanCode.setEnabled(false);
        this.binding.etProductCategory.setEnabled(false);
        this.binding.etProductDescription.setEnabled(false);
        this.binding.etProductBuyPrice.setEnabled(false);
        this.binding.etProductSellPrice.setEnabled(false);
        this.binding.etProductStock.setEnabled(false);
        this.binding.etProductWeight.setEnabled(false);
        this.binding.etProductWeightUnit.setEnabled(false);
        this.binding.etSupplier.setEnabled(false);
        this.binding.tvChooseImage.setEnabled(false);
        this.binding.ivProduct.setEnabled(false);

        this.binding.tvUpdateProduct.setVisibility(View.GONE);
        this.binding.tvEditProduct.setOnClickListener(view -> {
            this.binding.etProductName.setEnabled(true);
            etProductCode.setEnabled(true);
            this.binding.ivScanCode.setEnabled(true);
            this.binding.etProductCategory.setEnabled(true);
            this.binding.etProductDescription.setEnabled(true);
            this.binding.etProductBuyPrice.setEnabled(true);
            this.binding.etProductSellPrice.setEnabled(true);
            this.binding.etProductStock.setEnabled(true);
            this.binding.etProductWeight.setEnabled(true);
            this.binding.etProductWeightUnit.setEnabled(true);
            this.binding.etSupplier.setEnabled(true);
            this.binding.tvChooseImage.setEnabled(true);
            this.binding.ivProduct.setEnabled(true);

            this.binding.etProductName.setTextColor(SupportMenu.CATEGORY_MASK);
            etProductCode.setTextColor(SupportMenu.CATEGORY_MASK);
            this.binding.etProductCategory.setTextColor(SupportMenu.CATEGORY_MASK);
            this.binding.etProductDescription.setTextColor(SupportMenu.CATEGORY_MASK);
            this.binding.etProductBuyPrice.setTextColor(SupportMenu.CATEGORY_MASK);
            this.binding.etProductSellPrice.setTextColor(SupportMenu.CATEGORY_MASK);
            this.binding.etProductStock.setTextColor(SupportMenu.CATEGORY_MASK);
            this.binding.etProductWeight.setTextColor(SupportMenu.CATEGORY_MASK);
            this.binding.etProductWeightUnit.setTextColor(SupportMenu.CATEGORY_MASK);
            this.binding.etSupplier.setTextColor(SupportMenu.CATEGORY_MASK);

            this.binding.tvEditProduct.setVisibility(View.GONE);
            this.binding.tvUpdateProduct.setVisibility(View.VISIBLE);
        });

        this.binding.ivScanCode.setOnClickListener(view -> this.startActivity(new Intent(EditProductActivity.this, EditProductScannerViewActivity.class)));

        this.binding.tvChooseImage.setOnClickListener(view -> {
            Intent i = new Intent(EditProductActivity.this, ImageSelectActivity.class);
            i.putExtra(ImageSelectActivity.FLAG_COMPRESS, true);
            i.putExtra(ImageSelectActivity.FLAG_CAMERA, true);
            i.putExtra(ImageSelectActivity.FLAG_GALLERY, true);
            this.startActivityForResult(i, 1213);
        });

        this.binding.ivProduct.setOnClickListener(view -> {
            Intent i = new Intent(EditProductActivity.this, ImageSelectActivity.class);
            i.putExtra(ImageSelectActivity.FLAG_COMPRESS, true);
            i.putExtra(ImageSelectActivity.FLAG_CAMERA, true);
            i.putExtra(ImageSelectActivity.FLAG_GALLERY, true);
            startActivityForResult(i, 1213);
        });

        this.categoryNames = new ArrayList<>();
        this.weightUnitNames = new ArrayList<>();
        this.supplierNames = new ArrayList<>();

        databaseAccess = DatabaseAccess.getInstance(this);
        databaseAccess.open();
        List<HashMap<String, String>> productData = databaseAccess.getProductsInfo(this.productID);
        String product_categoryID = productData.get(0).get(Constant.PRODUCT_CATEGORY);
        String product_weightUnitID = productData.get(0).get(Constant.PRODUCT_WEIGHT_UNIT_ID);
        String product_supplierID = productData.get(0).get(Constant.PRODUCT_SUPPLIER);

        this.binding.etProductName.setText(productData.get(0).get(Constant.PRODUCT_NAME));
        this.binding.etProductCode.setText(productData.get(0).get(Constant.PRODUCT_CODE));
        databaseAccess.open();
        this.binding.etProductCategory.setText(databaseAccess.getCategoryName(product_categoryID));
        this.binding.etProductDescription.setText(productData.get(0).get(Constant.PRODUCT_DESCRIPTION));
        this.binding.etProductBuyPrice.setText(productData.get(0).get(Constant.PRODUCT_BUY_PRICE));
        this.binding.etProductSellPrice.setText(productData.get(0).get(Constant.PRODUCT_SELL_PRICE));
        this.binding.etProductStock.setText(productData.get(0).get(Constant.PRODUCT_STOCK));
        this.binding.etProductWeight.setText(productData.get(0).get(Constant.PRODUCT_WEIGHT));
        databaseAccess.open();
        this.binding.etProductWeightUnit.setText(databaseAccess.getWeightUnitName(product_weightUnitID));
        databaseAccess.open();
        this.binding.etSupplier.setText(databaseAccess.getSupplierName(product_supplierID));

        String product_image = productData.get(0).get(Constant.PRODUCT_IMAGE);
        if (product_image != null) {
            if (product_image.length() < 6) {
                this.binding.ivProduct.setImageResource(R.drawable.image_placeholder);
            } else {
                byte[] bytes = Base64.decode(product_image, Base64.DEFAULT);
                this.binding.ivProduct.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
            }
        }
        this.encodedImage = product_image;

        this.selectedCategoryID = product_categoryID;
        this.selectedWeightUnitID = product_weightUnitID;
        this.selectedSupplierID = product_supplierID;

        databaseAccess.open();
        final List<HashMap<String, String>> productCategory = databaseAccess.getProductCategory();
        for (int i = 0; i < productCategory.size(); i++) {
            this.categoryNames.add(productCategory.get(i).get(Constant.CATEGORY_NAME));
        }

        databaseAccess.open();
        final List<HashMap<String, String>> weightUnit = databaseAccess.getWeightUnit();
        for (int i1 = 0; i1 < weightUnit.size(); i1++) {
            this.weightUnitNames.add(weightUnit.get(i1).get(Constant.WEIGHT_UNIT));
        }

        databaseAccess.open();
        final List<HashMap<String, String>> productSupplier = databaseAccess.getProductSupplier();
        for (int i2 = 0; i2 < productSupplier.size(); i2++) {
            this.supplierNames.add(productSupplier.get(i2).get(Constant.SUPPLIERS_NAME));
        }

        this.binding.etProductCategory.setOnClickListener(view -> {
            this.categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
            this.categoryAdapter.addAll(this.categoryNames);

            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            View dialogView = this.getLayoutInflater().inflate(R.layout.dialog_list_search, null);
            dialog.setView(dialogView);
            dialog.setCancelable(false);

            TextView title = dialogView.findViewById(R.id.tv_dialog_title);
            EditText search = dialogView.findViewById(R.id.et_dialog_search);
            ListView dialogListView = dialogView.findViewById(R.id.dialog_listView);
            Button btnCancel = dialogView.findViewById(R.id.btn_dialog_cancel);

            title.setText(R.string.product_category);
            dialogListView.setVerticalScrollBarEnabled(true);
            dialogListView.setAdapter(this.categoryAdapter);
            search.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                    categoryAdapter.getFilter().filter(charSequence);
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
            final AlertDialog alertDialog = dialog.create();
            btnCancel.setOnClickListener(view1 -> alertDialog.dismiss());
            alertDialog.show();
            dialogListView.setOnItemClickListener((adapterView, view1, i, l) -> {
                alertDialog.dismiss();
                String selectedItem = this.categoryAdapter.getItem(i);
                String category_id = "0";
                this.binding.etProductCategory.setText(selectedItem);
                for (int i3 = 0; i3 < this.categoryNames.size(); i3++) {
                    if (this.categoryNames.get(i3).equalsIgnoreCase(selectedItem)) {
                        category_id = (String) ((HashMap) productCategory.get(i3)).get(Constant.CATEGORY_ID);
                    }
                }
                this.selectedCategoryID = category_id;
                Log.d(Constant.CATEGORY_ID, category_id);
            });
        });

        this.binding.etProductWeightUnit.setOnClickListener(view -> {
            this.weightUnitAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
            this.weightUnitAdapter.addAll(this.weightUnitNames);

            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            View dialogView = this.getLayoutInflater().inflate(R.layout.dialog_list_search, null);
            dialog.setView(dialogView);
            dialog.setCancelable(false);

            TextView title = dialogView.findViewById(R.id.tv_dialog_title);
            EditText search = dialogView.findViewById(R.id.et_dialog_search);
            ListView dialogListView = dialogView.findViewById(R.id.dialog_listView);
            Button btnCancel = dialogView.findViewById(R.id.btn_dialog_cancel);

            title.setText(R.string.product_weight_unit);
            dialogListView.setVerticalScrollBarEnabled(true);
            dialogListView.setAdapter(this.weightUnitAdapter);
            search.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                    weightUnitAdapter.getFilter().filter(charSequence);
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
            final AlertDialog alertDialog = dialog.create();
            btnCancel.setOnClickListener(view13 -> alertDialog.dismiss());
            alertDialog.show();
            dialogListView.setOnItemClickListener((adapterView, view11, i, l) -> {
                alertDialog.dismiss();
                String selectedItem = this.weightUnitAdapter.getItem(i);
                String weight_unit_id = "0";
                this.binding.etProductWeightUnit.setText(selectedItem);
                for (int i4 = 0; i4 < this.weightUnitNames.size(); i4++) {
                    if (this.weightUnitNames.get(i4).equalsIgnoreCase(selectedItem)) {
                        weight_unit_id = (String) ((HashMap) weightUnit.get(i4)).get(Constant.WEIGHT_ID);
                    }
                }
                this.selectedWeightUnitID = weight_unit_id;
                Log.d(Constant.WEIGHT_UNIT, this.selectedWeightUnitID);
            });
        });

        this.binding.etSupplier.setOnClickListener(view -> {
            this.supplierAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
            this.supplierAdapter.addAll(this.supplierNames);

            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            View dialogView = this.getLayoutInflater().inflate(R.layout.dialog_list_search, null);
            dialog.setView(dialogView);
            dialog.setCancelable(false);

            TextView title = dialogView.findViewById(R.id.tv_dialog_title);
            EditText search = dialogView.findViewById(R.id.et_dialog_search);
            ListView dialogListView = dialogView.findViewById(R.id.dialog_listView);
            Button btnCancel = dialogView.findViewById(R.id.btn_dialog_cancel);

            title.setText(R.string.suppliers);
            dialogListView.setVerticalScrollBarEnabled(true);
            dialogListView.setAdapter(this.supplierAdapter);
            search.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                    supplierAdapter.getFilter().filter(charSequence);
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
            final AlertDialog alertDialog = dialog.create();
            btnCancel.setOnClickListener(view12 -> alertDialog.dismiss());
            alertDialog.show();
            dialogListView.setOnItemClickListener((adapterView, view111, i, l) -> {
                alertDialog.dismiss();
                String selectedItem = this.supplierAdapter.getItem(i);
                String supplier_id = "0";
                this.binding.etSupplier.setText(selectedItem);
                for (int i5 = 0; i5 < this.supplierNames.size(); i5++) {
                    if (this.supplierNames.get(i5).equalsIgnoreCase(selectedItem)) {
                        supplier_id = (String) ((HashMap) productSupplier.get(i5)).get(Constant.SUPPLIERS_ID);
                    }
                }
                this.selectedSupplierID = supplier_id;
                Log.d(Constant.SUPPLIERS_ID, selectedSupplierID);
            });
        });

        this.binding.tvUpdateProduct.setOnClickListener(view -> {
            String product_name = this.binding.etProductName.getText().toString();
            String product_code = this.binding.etProductCode.getText().toString();
            String product_category = this.selectedCategoryID;
            String product_description = this.binding.etProductDescription.getText().toString();
            String product_buyPrice = this.binding.etProductBuyPrice.getText().toString();
            String product_sellPrice = this.binding.etProductSellPrice.getText().toString();
            String product_stock = this.binding.etProductStock.getText().toString();
            String product_weight = this.binding.etProductWeight.getText().toString();
            String product_weightUnit = this.selectedWeightUnitID;
            String product_supplier = this.selectedSupplierID;

            if (product_name.isEmpty()) {
                this.binding.etProductName.setError(this.getString(R.string.product_name_cannot_be_empty));
                this.binding.etProductName.requestFocus();
            } else if (product_category.isEmpty()) {
                this.binding.etProductCategory.setError(this.getString(R.string.product_category_cannot_be_empty));
                this.binding.etProductCategory.requestFocus();
            } else if (product_sellPrice.isEmpty()) {
                this.binding.etProductSellPrice.setError(this.getString(R.string.product_sell_price_cannot_be_empty));
                this.binding.etProductSellPrice.requestFocus();
            } else if (product_stock.isEmpty()) {
                this.binding.etProductStock.setError(this.getString(R.string.product_stock_cannot_be_empty));
                this.binding.etProductStock.requestFocus();
            } else if (product_weight.isEmpty()) {
                this.binding.etProductWeight.setError(this.getString(R.string.product_weight_cannot_be_empty));
                this.binding.etProductWeight.requestFocus();
            } else if (product_supplier.isEmpty()) {
                this.binding.etSupplier.setError(this.getString(R.string.product_supplier_cannot_be_empty));
                this.binding.etSupplier.requestFocus();
            } else {
                databaseAccess.open();
                boolean check = databaseAccess.updateProduct(product_name, product_code, product_category, product_description, product_buyPrice, product_sellPrice, product_stock, product_supplier, this.encodedImage, product_weightUnit, product_weight, this.productID);
                if (check) {
                    Toasty.success(this, R.string.update_successfully, Toasty.LENGTH_SHORT).show();
                    Intent i = new Intent(EditProductActivity.this, ProductActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    this.startActivity(i);
                    return;
                }
                Toasty.error(this, R.string.failed, Toasty.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1213 && resultCode == Activity.RESULT_OK && data != null) {
            try {
                this.mediaPath = data.getStringExtra(ImageSelectActivity.RESULT_FILE_PATH);
                Bitmap selectedImage = BitmapFactory.decodeFile(this.mediaPath);
                this.binding.ivProduct.setImageBitmap(selectedImage);
                this.encodedImage = encodeImage(selectedImage);
            } catch (Exception e) {
                Toast.makeText(this, R.string.something_went_wrong, Toast.LENGTH_LONG).show();
            }
        }
    }

    private String encodeImage(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
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