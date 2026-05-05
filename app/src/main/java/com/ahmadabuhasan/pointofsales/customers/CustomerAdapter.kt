package com.ahmadabuhasan.pointofsales.customers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmadabuhasan.pointofsales.Constant;
import com.ahmadabuhasan.pointofsales.R;
import com.ahmadabuhasan.pointofsales.database.DatabaseAccess;
import com.ahmadabuhasan.pointofsales.databinding.CustomerItemBinding;

import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;

/*
 * Created by Ahmad Abu Hasan (C) 2022
 */

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.MyViewHolder> {

    private final Context context;
    private final List<HashMap<String, String>> customerData;

    public CustomerAdapter(Context context1, List<HashMap<String, String>> customerData1) {
        this.context = context1;
        this.customerData = customerData1;
    }

    @NonNull
    @Override
    public CustomerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CustomerItemBinding binding = CustomerItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerAdapter.MyViewHolder holder, int position) {

        final String customer_id = this.customerData.get(position).get(Constant.CUSTOMER_ID);
        final String customer_cell = this.customerData.get(position).get(Constant.CUSTOMER_CELL);

        holder.binding.tvCustomerName.setText(this.customerData.get(position).get(Constant.CUSTOMER_NAME));
        holder.binding.tvCustomerCell.setText(customer_cell);
        holder.binding.tvCustomerEmail.setText(this.customerData.get(position).get(Constant.CUSTOMER_EMAIL));
        holder.binding.tvCustomerAddress.setText(this.customerData.get(position).get(Constant.CUSTOMER_ADDRESS));

        holder.binding.ivCustomerCall.setOnClickListener(view -> {
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            String phone = "tel:" + customer_cell;
            callIntent.setData(Uri.parse(phone));
            this.context.startActivity(callIntent);
        });

        holder.binding.ivCustomerDelete.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
            builder.setMessage(R.string.want_to_delete_customer)
                    .setCancelable(false)
                    .setPositiveButton(R.string.yes, (dialogInterface, i) -> {
                        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this.context);

                        databaseAccess.open();
                        if (databaseAccess.deleteCustomer(customer_id)) {
                            Toasty.error(this.context, R.string.customer_deleted, Toasty.LENGTH_SHORT).show();
                            this.customerData.remove(holder.getBindingAdapterPosition());
                            this.notifyItemRemoved(holder.getBindingAdapterPosition());
                        } else {
                            Toast.makeText(this.context, R.string.failed, Toast.LENGTH_SHORT).show();
                        }
                        dialogInterface.cancel();
                    }).setNegativeButton(R.string.no, (dialogInterface, i) -> dialogInterface.cancel()).show();
        });
    }

    @Override
    public int getItemCount() {
        return this.customerData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final CustomerItemBinding binding;

        public MyViewHolder(@NonNull CustomerItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Intent i = new Intent(CustomerAdapter.this.context, EditCustomersActivity.class);
            i.putExtra(Constant.CUSTOMER_ID, CustomerAdapter.this.customerData.get(getAbsoluteAdapterPosition()).get(Constant.CUSTOMER_ID));
            i.putExtra(Constant.CUSTOMER_NAME, CustomerAdapter.this.customerData.get(getAbsoluteAdapterPosition()).get(Constant.CUSTOMER_NAME));
            i.putExtra(Constant.CUSTOMER_CELL, CustomerAdapter.this.customerData.get(getAbsoluteAdapterPosition()).get(Constant.CUSTOMER_CELL));
            i.putExtra(Constant.CUSTOMER_EMAIL, CustomerAdapter.this.customerData.get(getAbsoluteAdapterPosition()).get(Constant.CUSTOMER_EMAIL));
            i.putExtra(Constant.CUSTOMER_ADDRESS, CustomerAdapter.this.customerData.get(getAbsoluteAdapterPosition()).get(Constant.CUSTOMER_ADDRESS));
            context.startActivity(i);
        }
    }
}