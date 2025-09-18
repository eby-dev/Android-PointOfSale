package com.ahmadabuhasan.pointofsales.pos;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmadabuhasan.pointofsales.Constant;
import com.ahmadabuhasan.pointofsales.R;
import com.ahmadabuhasan.pointofsales.database.DatabaseAccess;
import com.ahmadabuhasan.pointofsales.databinding.ProductCategoryItemBinding;
import com.ahmadabuhasan.pointofsales.settings.categories.EditCategoryActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.HashMap;
import java.util.List;

/*
 * Created by Ahmad Abu Hasan (C) 2022
 */

public class ProductCategoryAdapter extends RecyclerView.Adapter<ProductCategoryAdapter.MyViewHolder> {

    private final Context context;
    private final List<HashMap<String, String>> categoryData;
    RecyclerView recyclerView;
    ImageView ivNoData;
    TextView tvNoData;
    MediaPlayer sound;

    public ProductCategoryAdapter(Context context1,
                                  List<HashMap<String, String>> categoryData1,
                                  RecyclerView recyclerView1,
                                  ImageView ivNoData1,
                                  TextView tvNoData1) {
        this.context = context1;
        this.categoryData = categoryData1;
        this.tvNoData = tvNoData1;
        this.ivNoData = ivNoData1;
        this.recyclerView = recyclerView1;
        this.sound = MediaPlayer.create(context1, R.raw.delete_sound);
    }

    @NonNull
    @Override
    public ProductCategoryAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ProductCategoryItemBinding binding = ProductCategoryItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductCategoryAdapter.MyViewHolder holder, int position) {

        final DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this.context);

        final String category_id = this.categoryData.get(position).get(Constant.CATEGORY_ID);
        String category_name = this.categoryData.get(position).get(Constant.CATEGORY_NAME);

        holder.binding.tvCategoryName.setText(category_name);
        holder.binding.cvCategory.setOnClickListener(view -> {
            ProductCategoryAdapter.this.sound.start();

            databaseAccess.open();
            List<HashMap<String, String>> fromCategoryList = databaseAccess.getTabProducts(category_id);
            if (fromCategoryList.size() <= 0) {
                this.recyclerView.setVisibility(View.GONE);
                this.tvNoData.setVisibility(View.VISIBLE);
                this.ivNoData.setVisibility(View.VISIBLE);
                Glide.with(holder.itemView.getContext())
                        .load(R.drawable.not_found)
                        .apply(RequestOptions.placeholderOf(R.drawable.ic_loading).error(R.drawable.image_placeholder))
                        .into(ivNoData);
            } else {
                this.tvNoData.setVisibility(View.GONE);
                this.ivNoData.setVisibility(View.GONE);
                this.recyclerView.setVisibility(View.VISIBLE);

                PosProductAdapter adapter = new PosProductAdapter(ProductCategoryAdapter.this.context, fromCategoryList);
                this.recyclerView.setAdapter(adapter);
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.categoryData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final ProductCategoryItemBinding binding;

        public MyViewHolder(@NonNull ProductCategoryItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Intent i = new Intent(ProductCategoryAdapter.this.context, EditCategoryActivity.class);
            i.putExtra(Constant.CATEGORY_ID, ProductCategoryAdapter.this.categoryData.get(getAbsoluteAdapterPosition()).get(Constant.CATEGORY_ID));
            i.putExtra(Constant.CATEGORY_NAME, ProductCategoryAdapter.this.categoryData.get(getAbsoluteAdapterPosition()).get(Constant.CATEGORY_NAME));
        }
    }
}