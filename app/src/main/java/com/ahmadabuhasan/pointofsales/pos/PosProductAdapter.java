package com.ahmadabuhasan.pointofsales.pos;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmadabuhasan.pointofsales.Constant;
import com.ahmadabuhasan.pointofsales.R;
import com.ahmadabuhasan.pointofsales.database.DatabaseAccess;
import com.ahmadabuhasan.pointofsales.databinding.PosProductItemBinding;
import com.ahmadabuhasan.pointofsales.product.EditProductActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

/*
 * Created by Ahmad Abu Hasan (C) 2022
 */

public class PosProductAdapter extends RecyclerView.Adapter<PosProductAdapter.MyViewHolder> {

    private final Context context;
    private final List<HashMap<String, String>> productData;
    MediaPlayer sound;

    Locale locale = new Locale("in", "ID");
    NumberFormat formatIDR = NumberFormat.getInstance(locale);

    public PosProductAdapter(Context context1, List<HashMap<String, String>> productData1) {
        this.context = context1;
        this.productData = productData1;
        this.sound = MediaPlayer.create(context1, R.raw.delete_sound);
    }

    @NonNull
    @Override
    public PosProductAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        PosProductItemBinding binding = PosProductItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PosProductAdapter.MyViewHolder holder, int position) {

        final DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this.context);

        final String product_id = this.productData.get(position).get(Constant.PRODUCT_ID);
        String product_name = this.productData.get(position).get(Constant.PRODUCT_NAME);
        final String product_stock = this.productData.get(position).get(Constant.PRODUCT_STOCK);
        final String product_weight = this.productData.get(position).get(Constant.PRODUCT_WEIGHT);
        final String product_weightUnitID = this.productData.get(position).get(Constant.PRODUCT_WEIGHT_UNIT_ID);
        final String product_price = this.productData.get(position).get(Constant.PRODUCT_SELL_PRICE);
        String base64Image = this.productData.get(position).get(Constant.PRODUCT_IMAGE);

        holder.binding.tvProductName.setText(product_name);

        int getStock = Integer.parseInt(Objects.requireNonNull(product_stock));
        holder.binding.tvStock.setText(String.format("%s: %s", this.context.getString(R.string.stock), product_stock));
        if (getStock <= 5) {
            holder.binding.tvStock.setTextColor(Color.RED);
        }

        databaseAccess.open();
        String weightUnit_name = databaseAccess.getWeightUnitName(product_weightUnitID);
        holder.binding.tvWeight.setText(String.format("%s %s", product_weight, weightUnit_name));

        databaseAccess.open();
        String currency = databaseAccess.getCurrency();
        double convert = Double.parseDouble(Objects.requireNonNull(product_price));
        holder.binding.tvPrice.setText(String.format("%s%s", currency, formatIDR.format(convert)));

        if (base64Image != null) {
            if (base64Image.length() < 6) {
                Log.d("64base", base64Image);
                Glide.with(holder.itemView.getContext())
                        .load(base64Image)
                        .fitCenter()
                        .apply(RequestOptions.placeholderOf(R.drawable.ic_loading).error(R.drawable.image_placeholder))
                        .into(holder.binding.ivProduct);
            } else {
                byte[] bytes = Base64.decode(base64Image, Base64.DEFAULT);
                holder.binding.ivProduct.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
            }
        }

        holder.binding.cvProduct.setOnClickListener(view -> {
            PosProductAdapter.this.sound.start();
            Intent i = new Intent(PosProductAdapter.this.context, EditProductActivity.class);
            i.putExtra(Constant.PRODUCT_ID, product_id);
            this.context.startActivity(i);
        });

        holder.binding.btnAddCart.setOnClickListener(view -> {
            if (getStock <= 0) {
                Toasty.warning(this.context, R.string.stock_is_low_please_update_stock, Toasty.LENGTH_SHORT).show();
                return;
            }
            Log.d("weightUnitID", product_weightUnitID);

            databaseAccess.open();
            int check = databaseAccess.addToCart(product_id, product_weight, product_weightUnitID, product_price, 1, product_stock);
            if (check == 1) {
                Toasty.success(this.context, R.string.product_added_to_cart, Toasty.LENGTH_SHORT).show();
                this.sound.start();
            } else if (check == 2) {
                Toasty.info(this.context, R.string.product_already_added_to_cart, Toasty.LENGTH_SHORT).show();
            } else {
                Toasty.error(this.context, R.string.product_added_to_cart_failed_try_again, Toasty.LENGTH_SHORT).show();
            }

            databaseAccess.open();
            int count1 = databaseAccess.getCartItemCount();
            if (count1 == 0) {
                PosActivity.tvCount.setVisibility(View.INVISIBLE);
            } else {
                PosActivity.tvCount.setVisibility(View.VISIBLE);
                PosActivity.tvCount.setText(String.valueOf(count1));
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.productData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private final PosProductItemBinding binding;

        public MyViewHolder(@NonNull PosProductItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}