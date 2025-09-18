package com.ahmadabuhasan.pointofsales.settings.order_type;

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
import com.ahmadabuhasan.pointofsales.databinding.DeliveryItemBinding;

import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;

/*
 * Created by Ahmad Abu Hasan (C) 2022
 */

public class DeliveryAdapter extends RecyclerView.Adapter<DeliveryAdapter.MyViewHolder> {

    private final Context context;
    private final List<HashMap<String, String>> deliveryData;

    public DeliveryAdapter(Context context1, List<HashMap<String, String>> deliveryData1) {
        this.context = context1;
        this.deliveryData = deliveryData1;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        DeliveryItemBinding binding = DeliveryItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        final String delivery_id = this.deliveryData.get(position).get(Constant.ORDER_TYPE_ID);

        holder.binding.tvDeliveryName.setText(this.deliveryData.get(position).get(Constant.ORDER_TYPE_NAME));
        holder.binding.ivDelete.setOnClickListener(view -> new AlertDialog.Builder(this.context)
                .setMessage(R.string.want_to_delete)
                .setCancelable(false)
                .setPositiveButton(R.string.yes, (dialogInterface, i) -> {
                    DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this.context);

                    databaseAccess.open();
                    if (databaseAccess.deleteOrderType(delivery_id)) {
                        Toasty.success(this.context, R.string.delivery_deleted, Toasty.LENGTH_SHORT).show();
                        this.deliveryData.remove(holder.getAdapterPosition());
                        this.notifyItemRemoved(holder.getAdapterPosition());
                    } else {
                        Toasty.error(this.context, R.string.failed, Toasty.LENGTH_SHORT).show();
                    }
                    dialogInterface.cancel();
                }).setNegativeButton(R.string.no, (dialogInterface, i) -> dialogInterface.cancel()).show());
    }

    @Override
    public int getItemCount() {
        return this.deliveryData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final DeliveryItemBinding binding;

        public MyViewHolder(@NonNull DeliveryItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Intent i = new Intent(context, EditDeliveryActivity.class);
            i.putExtra(Constant.ORDER_TYPE_ID, deliveryData.get(getAdapterPosition()).get(Constant.ORDER_TYPE_ID));
            i.putExtra(Constant.ORDER_TYPE_NAME, deliveryData.get(getAdapterPosition()).get(Constant.ORDER_TYPE_NAME));
            context.startActivity(i);
        }
    }
}