package com.ahmadabuhasan.pointofsales.settings.categories;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.internal.view.SupportMenu;

import com.ahmadabuhasan.pointofsales.Constant;
import com.ahmadabuhasan.pointofsales.R;
import com.ahmadabuhasan.pointofsales.database.DatabaseAccess;
import com.ahmadabuhasan.pointofsales.databinding.ActivityEditCategoryBinding;
import com.ahmadabuhasan.pointofsales.utils.BaseActivity;

import java.util.Objects;

import es.dmoral.toasty.Toasty;

/*
 * Created by Ahmad Abu Hasan (C) 2022
 */

public class EditCategoryActivity extends BaseActivity {

    private ActivityEditCategoryBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditCategoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.edit_category);

        final String category_id = getIntent().getExtras().getString(Constant.CATEGORY_ID);

        this.binding.etCategoryName.setText(getIntent().getExtras().getString(Constant.CATEGORY_NAME));
        this.binding.etCategoryName.setEnabled(false);

        this.binding.tvUpdateCategory.setVisibility(View.GONE);
        this.binding.tvEditCategory.setOnClickListener(view -> {
            this.binding.etCategoryName.setEnabled(true);
            this.binding.etCategoryName.setTextColor(SupportMenu.CATEGORY_MASK);
            this.binding.tvEditCategory.setVisibility(View.GONE);
            this.binding.tvUpdateCategory.setVisibility(View.VISIBLE);
        });

        this.binding.tvUpdateCategory.setOnClickListener(view -> {
            String category_name = this.binding.etCategoryName.getText().toString().trim();
            if (category_name.isEmpty()) {
                this.binding.etCategoryName.setError(this.getString(R.string.enter_category_name));
                this.binding.etCategoryName.requestFocus();
            } else {
                DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);

                databaseAccess.open();
                if (databaseAccess.updateCategory(category_id, category_name)) {
                    Toasty.success(this, R.string.category_updated, Toasty.LENGTH_SHORT).show();
                    Intent i = new Intent(EditCategoryActivity.this, CategoriesActivity.class);
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