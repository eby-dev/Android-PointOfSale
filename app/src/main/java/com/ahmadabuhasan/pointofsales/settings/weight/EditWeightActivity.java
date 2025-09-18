package com.ahmadabuhasan.pointofsales.settings.weight;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.internal.view.SupportMenu;

import com.ahmadabuhasan.pointofsales.Constant;
import com.ahmadabuhasan.pointofsales.R;
import com.ahmadabuhasan.pointofsales.database.DatabaseAccess;
import com.ahmadabuhasan.pointofsales.databinding.ActivityEditWeightBinding;
import com.ahmadabuhasan.pointofsales.utils.BaseActivity;

import java.util.Objects;

import es.dmoral.toasty.Toasty;

/*
 * Created by Ahmad Abu Hasan (C) 2022
 */

public class EditWeightActivity extends BaseActivity {

    private ActivityEditWeightBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditWeightBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.update_weight);

        final String weight_id = getIntent().getExtras().getString(Constant.WEIGHT_ID);

        this.binding.etWeUnNameName.setText(getIntent().getExtras().getString(Constant.WEIGHT_UNIT));
        this.binding.etWeUnNameName.setEnabled(false);

        this.binding.tvUpdateWeight.setVisibility(View.GONE);
        this.binding.tvEditWeight.setOnClickListener(view -> {
            EditWeightActivity.this.binding.etWeUnNameName.setEnabled(true);
            EditWeightActivity.this.binding.etWeUnNameName.setTextColor(SupportMenu.CATEGORY_MASK);
            EditWeightActivity.this.binding.tvEditWeight.setVisibility(View.GONE);
            EditWeightActivity.this.binding.tvUpdateWeight.setVisibility(View.VISIBLE);
        });

        this.binding.tvUpdateWeight.setOnClickListener(view -> {
            String weight_name = EditWeightActivity.this.binding.etWeUnNameName.getText().toString().trim();
            if (weight_name.isEmpty()) {
                EditWeightActivity.this.binding.etWeUnNameName.setError(EditWeightActivity.this.getString(R.string.enter_weight_name));
                EditWeightActivity.this.binding.etWeUnNameName.requestFocus();
            } else {
                DatabaseAccess databaseAccess = DatabaseAccess.getInstance(EditWeightActivity.this);

                databaseAccess.open();
                if (databaseAccess.updateWeight(weight_id, weight_name)) {
                    Toasty.success(EditWeightActivity.this, R.string.successfully_updated, Toasty.LENGTH_SHORT).show();
                    Intent i = new Intent(EditWeightActivity.this, WeightActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    EditWeightActivity.this.startActivity(i);
                } else {
                    Toasty.error(EditWeightActivity.this, R.string.failed, Toasty.LENGTH_SHORT).show();
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