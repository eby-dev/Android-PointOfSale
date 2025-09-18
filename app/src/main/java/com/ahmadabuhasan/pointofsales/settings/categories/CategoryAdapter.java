package com.ahmadabuhasan.pointofsales.settings.categories;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmadabuhasan.pointofsales.Constant;
import com.ahmadabuhasan.pointofsales.R;
import com.ahmadabuhasan.pointofsales.database.DatabaseAccess;
import com.ahmadabuhasan.pointofsales.databinding.CategoryItemBinding;

import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;

/*
 * Created by Ahmad Abu Hasan (C) 2022
 */

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyViewHolder> {

    private final Context context;
    private final List<HashMap<String, String>> categoryData;

    public CategoryAdapter(Context context1, List<HashMap<String, String>> categoryData1) {
        this.context = context1;
        this.categoryData = categoryData1;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CategoryItemBinding binding = CategoryItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        final String category_id = this.categoryData.get(position).get(Constant.CATEGORY_ID);

        holder.binding.tvCategoryName.setText(this.categoryData.get(position).get(Constant.CATEGORY_NAME));
        holder.binding.ivDelete.setOnClickListener(view -> new AlertDialog.Builder(this.context)
                .setMessage(R.string.want_to_delete_category)
                .setCancelable(false)
                .setPositiveButton(R.string.yes, (dialogInterface, i) -> {
                    DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this.context);

                    databaseAccess.open();
                    if (databaseAccess.deleteCategory(category_id)) {
                        Toasty.success(CategoryAdapter.this.context, R.string.category_deleted, Toasty.LENGTH_SHORT).show();
                        this.categoryData.remove(holder.getAdapterPosition());
                        this.notifyItemRemoved(holder.getAdapterPosition());
                    } else {
                        Toasty.error(this.context, R.string.failed, Toasty.LENGTH_SHORT).show();
                    }
                    dialogInterface.cancel();
                }).setNegativeButton(R.string.no, (dialogInterface, i) -> dialogInterface.cancel()).show());
    }

    @Override
    public int getItemCount() {
        return this.categoryData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final CategoryItemBinding binding;

        public MyViewHolder(@NonNull CategoryItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Intent i = new Intent(CategoryAdapter.this.context, EditCategoryActivity.class);
            i.putExtra(Constant.CATEGORY_ID, (String) ((HashMap) CategoryAdapter.this.categoryData.get(getAdapterPosition())).get(Constant.CATEGORY_ID));
            i.putExtra(Constant.CATEGORY_NAME, (String) ((HashMap) CategoryAdapter.this.categoryData.get(getAdapterPosition())).get(Constant.CATEGORY_NAME));
            context.startActivity(i);
        }
    }
}