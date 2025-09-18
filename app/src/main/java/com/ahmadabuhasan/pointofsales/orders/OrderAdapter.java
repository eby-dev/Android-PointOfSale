package com.ahmadabuhasan.pointofsales.orders;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmadabuhasan.pointofsales.Constant;
import com.ahmadabuhasan.pointofsales.R;
import com.ahmadabuhasan.pointofsales.database.DatabaseAccess;
import com.ahmadabuhasan.pointofsales.databinding.OrderItemBinding;
import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

/*
 * Created by Ahmad Abu Hasan (C) 2022
 */

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.MyViewHolder> {

    private final Context context;
    private final List<HashMap<String, String>> orderData;

    public OrderAdapter(Context context1, List<HashMap<String, String>> orderData1) {
        this.context = context1;
        this.orderData = orderData1;
    }

    @NonNull
    @Override
    public OrderAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        OrderItemBinding binding = OrderItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderAdapter.MyViewHolder holder, int position) {

        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(context);

        String customer_name = this.orderData.get(position).get(Constant.CUSTOMER_NAME);
        final String invoice_id = this.orderData.get(position).get(Constant.INVOICE_ID);
        String payment_method = this.orderData.get(position).get(Constant.ORDER_PAYMENT_METHOD);
        String order_type = this.orderData.get(position).get(Constant.ORDER_TYPE);
        String order_date = this.orderData.get(position).get(Constant.ORDER_DATE);
        String order_time = this.orderData.get(position).get(Constant.ORDER_TIME);
        String order_status = this.orderData.get(position).get(Constant.ORDER_STATUS);

        holder.binding.tvCustomerName.setText(customer_name);
        holder.binding.tvOrderId.setText(String.format("%s%s", this.context.getString(R.string.order_id), invoice_id));
        holder.binding.tvPaymentMethod.setText(String.format("%s%s", this.context.getString(R.string.payment_method), payment_method));
        holder.binding.tvOrderType.setText(String.format("%s:%s", this.context.getString(R.string.order_type), order_type));
        holder.binding.tvDate.setText(String.format("%s %s", order_time, order_date));
        holder.binding.tvOrderStatus.setText(order_status);

        if (Objects.requireNonNull(order_status).equals(Constant.COMPLETED)) {
            holder.binding.tvOrderStatus.setBackgroundColor(Color.parseColor("#43A047"));
            holder.binding.tvOrderStatus.setTextColor(Color.WHITE);
            holder.binding.ivEditStatus.setVisibility(View.GONE);
        } else if (order_status.equals(Constant.CANCEL)) {
            holder.binding.tvOrderStatus.setBackgroundColor(Color.parseColor("#E53935"));
            holder.binding.tvOrderStatus.setTextColor(Color.WHITE);
            holder.binding.ivEditStatus.setVisibility(View.GONE);
        }

        holder.binding.ivEditStatus.setOnClickListener(view -> {
            final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(context);
            dialogBuilder.withTitle(context.getString(R.string.change_order_status))
                    .withMessage(context.getString(R.string.please_change_order_status_to_complete_or_cancel))
                    .withEffect(Effectstype.Slidetop)
                    .withDialogColor("#01BAEF")
                    .withButton1Text(context.getString(R.string.order_completed))
                    .withButton2Text(context.getString(R.string.cancel_order))
                    .setButton1Click(view12 -> {
                        databaseAccess.open();
                        if (databaseAccess.updateOrder(invoice_id, Constant.COMPLETED)) {
                            Toasty.success(context, R.string.order_updated, Toasty.LENGTH_SHORT).show();
                            holder.binding.tvOrderStatus.setText(Constant.COMPLETED);
                            holder.binding.tvOrderStatus.setTextColor(Color.WHITE);
                            holder.binding.tvOrderStatus.setBackgroundColor(Color.parseColor("#43A047"));
                            holder.binding.ivEditStatus.setVisibility(View.GONE);
                        } else {
                            Toast.makeText(context, R.string.failed, Toast.LENGTH_SHORT).show();
                        }
                        dialogBuilder.dismiss();
                    }).setButton2Click(view1 -> {
                databaseAccess.open();
                if (databaseAccess.updateOrder(invoice_id, Constant.CANCEL)) {
                    Toasty.success(context, R.string.order_updated, Toasty.LENGTH_SHORT).show();
                    holder.binding.tvOrderStatus.setText(Constant.CANCEL);
                    holder.binding.tvOrderStatus.setTextColor(Color.WHITE);
                    holder.binding.tvOrderStatus.setBackgroundColor(Color.parseColor("#E53935"));
                    holder.binding.ivEditStatus.setVisibility(View.GONE);
                } else {
                    Toast.makeText(context, R.string.failed, Toast.LENGTH_SHORT).show();
                }
                dialogBuilder.dismiss();
            }).show();
        });

        holder.itemView.setOnLongClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("Want to Delete Order ?")
                    .setCancelable(false)
                    .setPositiveButton(R.string.yes, (dialog, which) -> {
                        databaseAccess.open();
                        if (databaseAccess.deleteOrder(invoice_id)) {
                            Toasty.error(context, "Order Deleted", Toast.LENGTH_SHORT).show();
                            orderData.remove(holder.getAdapterPosition());
                            notifyItemRemoved(holder.getAdapterPosition());
                        } else {
                            Toast.makeText(context, R.string.failed, Toast.LENGTH_SHORT).show();
                        }
                        dialog.cancel();
                    })
                    .setNegativeButton(R.string.no, (dialog, which) -> dialog.cancel()).show();
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return this.orderData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final OrderItemBinding binding;

        public MyViewHolder(@NonNull OrderItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Intent i = new Intent(OrderAdapter.this.context, OrderDetailsActivity.class);
            i.putExtra(Constant.ORDER_ID, (String) ((HashMap) OrderAdapter.this.orderData.get(getAdapterPosition())).get(Constant.INVOICE_ID));
            i.putExtra(Constant.CUSTOMER_NAME, (String) ((HashMap) OrderAdapter.this.orderData.get(getAdapterPosition())).get(Constant.CUSTOMER_NAME));
            i.putExtra(Constant.ORDER_DATE, (String) ((HashMap) OrderAdapter.this.orderData.get(getAdapterPosition())).get(Constant.ORDER_DATE));
            i.putExtra(Constant.ORDER_TIME, (String) ((HashMap) OrderAdapter.this.orderData.get(getAdapterPosition())).get(Constant.ORDER_TIME));
            i.putExtra(Constant.TAX, (String) ((HashMap) OrderAdapter.this.orderData.get(getAdapterPosition())).get(Constant.TAX));
            i.putExtra(Constant.DISCOUNT, (String) ((HashMap) OrderAdapter.this.orderData.get(getAdapterPosition())).get(Constant.DISCOUNT));
            context.startActivity(i);
        }
    }
}