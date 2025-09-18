package com.ahmadabuhasan.pointofsales.suppliers;

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
import com.ahmadabuhasan.pointofsales.databinding.SupplierItemBinding;

import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;

/*
 * Created by Ahmad Abu Hasan (C) 2022
 */

public class SupplierAdapter extends RecyclerView.Adapter<SupplierAdapter.MyViewHolder> {

    private final Context context;
    private final List<HashMap<String, String>> supplierData;

    public SupplierAdapter(Context context1, List<HashMap<String, String>> supplierData1) {
        this.context = context1;
        this.supplierData = supplierData1;
    }

    @NonNull
    @Override
    public SupplierAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        SupplierItemBinding binding = SupplierItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SupplierAdapter.MyViewHolder holder, int position) {

        final String supplier_id = this.supplierData.get(position).get(Constant.SUPPLIERS_ID);
        final String supplier_cell = this.supplierData.get(position).get(Constant.SUPPLIERS_CELL);

        holder.binding.tvSupplierName.setText(this.supplierData.get(position).get(Constant.SUPPLIERS_NAME));
        holder.binding.tvSupplierContactPerson.setText(this.supplierData.get(position).get(Constant.SUPPLIERS_CONTACT_PERSON));
        holder.binding.tvSupplierCell.setText(supplier_cell);
        holder.binding.tvSupplierEmail.setText(this.supplierData.get(position).get(Constant.SUPPLIERS_EMAIL));
        holder.binding.tvSupplierAddress.setText(this.supplierData.get(position).get(Constant.SUPPLIERS_ADDRESS));

        holder.binding.ivSupplierCall.setOnClickListener(view -> {
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            String phone = "tel:" + supplier_cell;
            callIntent.setData(Uri.parse(phone));
            this.context.startActivity(callIntent);
        });

        holder.binding.ivSupplierDelete.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
            builder.setMessage(R.string.want_to_delete_supplier)
                    .setCancelable(false)
                    .setPositiveButton(R.string.yes, (dialogInterface, i) -> {
                        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this.context);

                        databaseAccess.open();
                        if (databaseAccess.deleteSupplier(supplier_id)) {
                            Toasty.success(this.context, R.string.supplier_deleted, Toasty.LENGTH_SHORT).show();
                            this.supplierData.remove(holder.getAdapterPosition());
                            this.notifyItemRemoved(holder.getAdapterPosition());
                        } else {
                            Toast.makeText(this.context, R.string.failed, Toast.LENGTH_SHORT).show();
                        }
                        dialogInterface.cancel();
                    }).setNegativeButton(R.string.no, (dialogInterface, i) -> dialogInterface.cancel()).show();
        });
    }

    @Override
    public int getItemCount() {
        return this.supplierData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final SupplierItemBinding binding;

        public MyViewHolder(@NonNull SupplierItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Intent i = new Intent(SupplierAdapter.this.context, EditSuppliersActivity.class);
            i.putExtra(Constant.SUPPLIERS_ID, (String) ((HashMap) SupplierAdapter.this.supplierData.get(getAdapterPosition())).get(Constant.SUPPLIERS_ID));
            i.putExtra(Constant.SUPPLIERS_NAME, (String) ((HashMap) SupplierAdapter.this.supplierData.get(getAdapterPosition())).get(Constant.SUPPLIERS_NAME));
            i.putExtra(Constant.SUPPLIERS_CONTACT_PERSON, (String) ((HashMap) SupplierAdapter.this.supplierData.get(getAdapterPosition())).get(Constant.SUPPLIERS_CONTACT_PERSON));
            i.putExtra(Constant.SUPPLIERS_CELL, (String) ((HashMap) SupplierAdapter.this.supplierData.get(getAdapterPosition())).get(Constant.SUPPLIERS_CELL));
            i.putExtra(Constant.SUPPLIERS_EMAIL, (String) ((HashMap) SupplierAdapter.this.supplierData.get(getAdapterPosition())).get(Constant.SUPPLIERS_EMAIL));
            i.putExtra(Constant.SUPPLIERS_ADDRESS, (String) ((HashMap) SupplierAdapter.this.supplierData.get(getAdapterPosition())).get(Constant.SUPPLIERS_ADDRESS));
            context.startActivity(i);
        }
    }
}