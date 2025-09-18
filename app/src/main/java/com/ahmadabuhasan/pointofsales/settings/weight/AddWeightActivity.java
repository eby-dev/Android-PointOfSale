package com.ahmadabuhasan.pointofsales.settings.weight;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.ahmadabuhasan.pointofsales.R;
import com.ahmadabuhasan.pointofsales.database.DatabaseAccess;
import com.ahmadabuhasan.pointofsales.databinding.ActivityAddWeightBinding;
import com.ahmadabuhasan.pointofsales.utils.BaseActivity;

import java.util.Objects;

import es.dmoral.toasty.Toasty;

/*
 * Created by Ahmad Abu Hasan (C) 2022
 */

public class AddWeightActivity extends BaseActivity {

    private ActivityAddWeightBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddWeightBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.add_weight);

        this.binding.tvAddWeight.setOnClickListener(view -> {
            String weight_name = AddWeightActivity.this.binding.etWeUnNameName.getText().toString().trim();
            if (weight_name.isEmpty()) {
                AddWeightActivity.this.binding.etWeUnNameName.setError(AddWeightActivity.this.getString(R.string.enter_weight_name));
                AddWeightActivity.this.binding.etWeUnNameName.requestFocus();
            } else {
                DatabaseAccess databaseAccess = DatabaseAccess.getInstance(AddWeightActivity.this);

                databaseAccess.open();
                if (databaseAccess.addWeight(weight_name)) {
                    Toasty.success(AddWeightActivity.this, R.string.successfully_added, Toasty.LENGTH_SHORT).show();
                    Intent i = new Intent(AddWeightActivity.this, WeightActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    AddWeightActivity.this.startActivity(i);
                } else {
                    Toasty.error(AddWeightActivity.this, R.string.failed, Toasty.LENGTH_SHORT).show();
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