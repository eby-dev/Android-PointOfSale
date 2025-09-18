package com.ahmadabuhasan.pointofsales.orders;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmadabuhasan.pointofsales.Constant;
import com.ahmadabuhasan.pointofsales.R;
import com.ahmadabuhasan.pointofsales.database.DatabaseAccess;
import com.ahmadabuhasan.pointofsales.databinding.OrderDetailsItemBinding;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

/*
 * Created by Ahmad Abu Hasan (C) 2022
 */

public class OrderDetailsAdapter extends RecyclerView.Adapter<OrderDetailsAdapter.MyViewHolder> {

    private final Context context;
    private final List<HashMap<String, String>> orderData;
    DecimalFormat decimalFormat = new DecimalFormat("#0.00");

    public OrderDetailsAdapter(Context context1, List<HashMap<String, String>> orderData1) {
        this.context = context1;
        this.orderData = orderData1;
    }

    @NonNull
    @Override
    public OrderDetailsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        OrderDetailsItemBinding binding = OrderDetailsItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderDetailsAdapter.MyViewHolder holder, int position) {

        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this.context);

        holder.binding.tvProductName.setText(this.orderData.get(position).get(Constant.PRODUCT_NAME));
        holder.binding.tvQty.setText(String.format("%s%s", this.context.getString(R.string.quantity), this.orderData.get(position).get(Constant.PRODUCT_QTY)));
        holder.binding.tvWeight.setText(String.format("%s:%s", this.context.getString(R.string.weight), this.orderData.get(position).get(Constant.PRODUCT_WEIGHT)));

        String base64Image = this.orderData.get(position).get(Constant.PRODUCT_IMAGE);
        String unit_price = this.orderData.get(position).get(Constant.PRODUCT_PRICE);
        String qty = this.orderData.get(position).get(Constant.PRODUCT_QTY);

        double cost = Double.parseDouble(unit_price) * Integer.parseInt(qty);

        databaseAccess.open();
        String currency = databaseAccess.getCurrency();
        holder.binding.tvTotalCost.setText(String.format("%s%s x %s = %s%s", currency, unit_price, qty, currency, this.decimalFormat.format(cost)));

        if (base64Image == null) {
            return;
        }
        if (base64Image.isEmpty() || base64Image.length() < 6) {
            holder.binding.ivProduct.setImageResource(R.drawable.image_placeholder);
            return;
        }
        byte[] bytes = Base64.decode(base64Image, Base64.DEFAULT);
        holder.binding.ivProduct.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
    }

    @Override
    public int getItemCount() {
        return this.orderData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private final OrderDetailsItemBinding binding;

        public MyViewHolder(@NonNull OrderDetailsItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}