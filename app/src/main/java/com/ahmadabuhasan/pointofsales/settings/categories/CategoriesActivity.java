package com.ahmadabuhasan.pointofsales.settings.categories;

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
import com.ahmadabuhasan.pointofsales.databinding.ActivityCategoriesBinding;
import com.ahmadabuhasan.pointofsales.utils.BaseActivity;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

/*
 * Created by Ahmad Abu Hasan (C) 2022
 */

public class CategoriesActivity extends BaseActivity {

    private ActivityCategoriesBinding binding;
    DatabaseAccess databaseAccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCategoriesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.categories);

        this.binding.categoryRecyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        this.binding.categoryRecyclerview.setHasFixedSize(true);

        databaseAccess = DatabaseAccess.getInstance(this);
        databaseAccess.open();
        List<HashMap<String, String>> categoryData = databaseAccess.getProductCategory();
        Log.d("data", "" + categoryData.size());
        if (categoryData.size() <= 0) {
            Toasty.info(this, R.string.no_data_found, Toasty.LENGTH_SHORT).show();
            this.binding.ivNoCategory.setImageResource(R.drawable.no_data);
        } else {
            this.binding.ivNoCategory.setVisibility(View.GONE);
            this.binding.categoryRecyclerview.setAdapter(new CategoryAdapter(this, categoryData));
        }

        this.binding.etCategorySearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                databaseAccess.open();
                List<HashMap<String, String>> searchCategoryList = databaseAccess.searchProductCategory(charSequence.toString());
                if (searchCategoryList.size() <= 0) {
                    binding.categoryRecyclerview.setVisibility(View.GONE);
                    binding.ivNoCategory.setVisibility(View.VISIBLE);
                    binding.ivNoCategory.setImageResource(R.drawable.no_data);
                } else {
                    binding.ivNoCategory.setVisibility(View.GONE);
                    binding.categoryRecyclerview.setVisibility(View.VISIBLE);
                    binding.categoryRecyclerview.setAdapter(new CategoryAdapter(CategoriesActivity.this, searchCategoryList));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        this.binding.fabAdd.setOnClickListener(view -> this.startActivity(new Intent(CategoriesActivity.this, AddCategoryActivity.class)));
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