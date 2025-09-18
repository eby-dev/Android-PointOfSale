package com.ahmadabuhasan.pointofsales.settings.payment_method;

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
import com.ahmadabuhasan.pointofsales.databinding.PaymentMethodItemBinding;

import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;

/*
 * Created by Ahmad Abu Hasan (C) 2022
 */

public class PaymentMethodAdapter extends RecyclerView.Adapter<PaymentMethodAdapter.MyViewHolder> {

    private final Context context;
    private final List<HashMap<String, String>> paymentMethodData;

    public PaymentMethodAdapter(Context context1, List<HashMap<String, String>> paymentMethodData1) {
        this.context = context1;
        this.paymentMethodData = paymentMethodData1;
    }

    @NonNull
    @Override
    public PaymentMethodAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        PaymentMethodItemBinding binding = PaymentMethodItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PaymentMethodAdapter.MyViewHolder holder, int position) {

        final String payment_method_id = this.paymentMethodData.get(position).get(Constant.PAYMENT_METHOD_ID);

        holder.binding.tvPaymentMethodName.setText(this.paymentMethodData.get(position).get(Constant.PAYMENT_METHOD_NAME));
        holder.binding.ivDelete.setOnClickListener(view -> new AlertDialog.Builder(this.context)
                .setMessage(R.string.want_to_delete)
                .setCancelable(false)
                .setPositiveButton(R.string.yes, (dialogInterface, i) -> {
                    DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this.context);

                    databaseAccess.open();
                    if (databaseAccess.deletePaymentMethod(payment_method_id)) {
                        Toasty.success(this.context, R.string.payment_method_deleted, Toasty.LENGTH_SHORT).show();
                        this.paymentMethodData.remove(holder.getAdapterPosition());
                        this.notifyItemRemoved(holder.getAdapterPosition());
                    } else {
                        Toasty.error(this.context, R.string.failed, Toasty.LENGTH_SHORT).show();
                    }
                    dialogInterface.cancel();
                }).setNegativeButton(R.string.no, (dialogInterface, i) -> dialogInterface.cancel()).show());
    }

    @Override
    public int getItemCount() {
        return this.paymentMethodData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final PaymentMethodItemBinding binding;

        public MyViewHolder(@NonNull PaymentMethodItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Intent i = new Intent(PaymentMethodAdapter.this.context, EditPaymentMethodActivity.class);
            i.putExtra(Constant.PAYMENT_METHOD_ID, (String) ((HashMap) PaymentMethodAdapter.this.paymentMethodData.get(getAdapterPosition())).get(Constant.PAYMENT_METHOD_ID));
            i.putExtra(Constant.PAYMENT_METHOD_NAME, (String) ((HashMap) PaymentMethodAdapter.this.paymentMethodData.get(getAdapterPosition())).get(Constant.PAYMENT_METHOD_NAME));
            context.startActivity(i);
        }
    }
}