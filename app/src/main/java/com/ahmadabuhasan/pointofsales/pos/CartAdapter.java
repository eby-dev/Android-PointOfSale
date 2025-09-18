package com.ahmadabuhasan.pointofsales.pos;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmadabuhasan.pointofsales.Constant;
import com.ahmadabuhasan.pointofsales.R;
import com.ahmadabuhasan.pointofsales.database.DatabaseAccess;
import com.ahmadabuhasan.pointofsales.databinding.CartProductItemsBinding;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

/*
 * Created by Ahmad Abu Hasan (C) 2022
 */

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.MyViewHolder> {

    private final Context context;
    private final List<HashMap<String, String>> cartProduct;
    ImageView ivNoCart;
    TextView tvNoCart;
    TextView tvTotalPrice;
    Button btnSubmitOrder;

    MediaPlayer sound;
    public static Double total_price;

    Locale locale = new Locale("in", "ID");
    NumberFormat formatIDR = NumberFormat.getInstance(locale);

    public CartAdapter(Context context1,
                       List<HashMap<String, String>> cartProduct1,
                       ImageView ivNoData1,
                       TextView tvNoData1,
                       TextView tvTotalPrice1,
                       Button btnSubmitOrder1) {
        this.context = context1;
        this.cartProduct = cartProduct1;
        this.ivNoCart = ivNoData1;
        this.tvNoCart = tvNoData1;
        this.tvTotalPrice = tvTotalPrice1;
        this.btnSubmitOrder = btnSubmitOrder1;
        this.sound = MediaPlayer.create(context1, R.raw.delete_sound);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CartProductItemsBinding binding = CartProductItemsBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        final DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this.context);

        final String cart_id = this.cartProduct.get(position).get(Constant.CART_ID);
        String product_id = this.cartProduct.get(position).get(Constant.PRODUCT_ID);
        databaseAccess.open();
        String product_name = databaseAccess.getProductName(product_id);
        String weight = this.cartProduct.get(position).get(Constant.PRODUCT_WEIGHT);
        String weight_UnitID = this.cartProduct.get(position).get(Constant.PRODUCT_WEIGHT_UNIT);
        final String price = this.cartProduct.get(position).get(Constant.PRODUCT_PRICE);
        String qty = this.cartProduct.get(position).get(Constant.PRODUCT_QTY);
        String stock = this.cartProduct.get(position).get(Constant.STOCK);

        final int getStock = Integer.parseInt(Objects.requireNonNull(stock));

        databaseAccess.open();
        String base64Image = databaseAccess.getProductImage(product_id);

        databaseAccess.open();
        String weight_UnitName = databaseAccess.getWeightUnitName(weight_UnitID);

        databaseAccess.open();
        String currency = databaseAccess.getCurrency();

        databaseAccess.open();
        total_price = databaseAccess.getTotalPrice();
        this.tvTotalPrice.setText(String.format("%s %s%s", this.context.getString(R.string.total_price), currency, formatIDR.format(total_price)));

        if (base64Image != null) {
            if (base64Image.isEmpty() || base64Image.length() < 6) {
                Glide.with(holder.itemView.getContext())
                        .load(base64Image)
                        .apply(RequestOptions.placeholderOf(R.drawable.ic_loading).error(R.drawable.image_placeholder))
                        .into(holder.binding.ivCartProduct);
            } else {
                byte[] bytes = Base64.decode(base64Image, Base64.DEFAULT);
                holder.binding.ivCartProduct.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
            }
        }

        final double getPrice = Double.parseDouble(Objects.requireNonNull(price)) * Integer.parseInt(Objects.requireNonNull(qty));

        holder.binding.tvItemName.setText(product_name);
        holder.binding.tvWeight.setText(String.format("%s %s", weight, weight_UnitName));
        holder.binding.tvQtyNumber.setText(qty);
        holder.binding.tvPrice.setText(String.format("%s%s", currency, formatIDR.format(getPrice)));

        holder.binding.tvMinus.setOnClickListener(view -> {
            String qtyMinus = holder.binding.tvQtyNumber.getText().toString();
            int getQty = Integer.parseInt(qtyMinus);
            if (getQty >= 2) {
                getQty--;
                double cost = Double.parseDouble(price) * getQty;

                holder.binding.tvPrice.setText(String.format("%s%s", currency, formatIDR.format(cost)));
                holder.binding.tvQtyNumber.setText(MessageFormat.format("{0}", getQty));

                databaseAccess.open();
                databaseAccess.updateProductQty(cart_id, "" + getQty);
                total_price = total_price - Double.parseDouble(price);
                tvTotalPrice.setText(String.format("%s %s%s", this.context.getString(R.string.total_price), currency, formatIDR.format(total_price)));
            }
        });

        holder.binding.tvPlus.setOnClickListener(view -> {
            String qtyPlus = holder.binding.tvQtyNumber.getText().toString();
            int getQty = Integer.parseInt(qtyPlus);
            if (getQty >= getStock) {
                Toasty.error(context, R.string.available_stock, Toasty.LENGTH_SHORT).show();
            } else {
                getQty++;
                double cost = Double.parseDouble(price) * getQty;

                holder.binding.tvPrice.setText(String.format("%s%s", currency, formatIDR.format(cost)));
                holder.binding.tvQtyNumber.setText(MessageFormat.format("{0}", getQty));

                databaseAccess.open();
                databaseAccess.updateProductQty(cart_id, "" + getQty);
                total_price = total_price + Double.parseDouble(price);
                tvTotalPrice.setText(String.format("%s %s%s", this.context.getString(R.string.total_price), currency, formatIDR.format(total_price)));
            }
        });

        holder.binding.ivDelete.setOnClickListener(view -> {
            databaseAccess.open();
            if (databaseAccess.deleteProductFromCart(cart_id)) {
                Toasty.success(this.context, R.string.product_removed_from_cart, Toasty.LENGTH_SHORT).show();
                sound.start();
                cartProduct.remove(holder.getBindingAdapterPosition());
                notifyItemRemoved(holder.getBindingAdapterPosition());

                databaseAccess.open();
                total_price = databaseAccess.getTotalPrice();
                tvTotalPrice.setText(String.format("%s %s%s", this.context.getString(R.string.total_price), currency, formatIDR.format(total_price)));
            } else {
                Toasty.error(context, R.string.failed, Toasty.LENGTH_SHORT).show();
            }

            databaseAccess.open();
            int itemCount = databaseAccess.getCartItemCount();
            Log.d("itemCount", "" + itemCount);
            if (itemCount <= 0) {
                tvTotalPrice.setVisibility(View.GONE);
                btnSubmitOrder.setVisibility(View.GONE);

                ivNoCart.setVisibility(View.VISIBLE);
                tvNoCart.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.cartProduct.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private final CartProductItemsBinding binding;

        public MyViewHolder(@NonNull CartProductItemsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}