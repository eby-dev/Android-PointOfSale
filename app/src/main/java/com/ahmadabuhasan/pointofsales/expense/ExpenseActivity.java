package com.ahmadabuhasan.pointofsales.expense;

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
import com.ahmadabuhasan.pointofsales.databinding.ActivityExpenseBinding;
import com.ahmadabuhasan.pointofsales.utils.BaseActivity;
import com.ahmadabuhasan.pointofsales.utils.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

/*
 * Created by Ahmad Abu Hasan (C) 2022
 */

public class ExpenseActivity extends BaseActivity {

    private ActivityExpenseBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityExpenseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        new Utils().interstitialAdsShow(this);

        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.all_expense);

        this.binding.expenseRecyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        this.binding.expenseRecyclerview.setHasFixedSize(true);

        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);
        databaseAccess.open();
        List<HashMap<String, String>> expenseData = databaseAccess.getAllExpense();
        Log.d("data", "" + expenseData.size());
        if (expenseData.size() <= 0) {
            Toasty.info(this, R.string.no_data_found, Toasty.LENGTH_SHORT).show();
            this.binding.ivNoExpense.setImageResource(R.drawable.no_data);
        } else {
            this.binding.ivNoExpense.setVisibility(View.GONE);

            ExpenseAdapter adapter = new ExpenseAdapter(this, expenseData);
            this.binding.expenseRecyclerview.setAdapter(adapter);
        }

        this.binding.etExpenseSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                databaseAccess.open();
                List<HashMap<String, String>> searchExpenseList = databaseAccess.searchExpense(charSequence.toString());
                if (searchExpenseList.size() <= 0) {
                    binding.expenseRecyclerview.setVisibility(View.GONE);
                    binding.ivNoExpense.setVisibility(View.VISIBLE);
                    binding.ivNoExpense.setImageResource(R.drawable.no_data);
                } else {
                    binding.ivNoExpense.setVisibility(View.GONE);
                    binding.expenseRecyclerview.setVisibility(View.VISIBLE);

                    ExpenseAdapter adapter1 = new ExpenseAdapter(ExpenseActivity.this, searchExpenseList);
                    binding.expenseRecyclerview.setAdapter(adapter1);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        this.binding.fabAdd.setOnClickListener(view -> this.startActivity(new Intent(ExpenseActivity.this, AddExpenseActivity.class)));
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