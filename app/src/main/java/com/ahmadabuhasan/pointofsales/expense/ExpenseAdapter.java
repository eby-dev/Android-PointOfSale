package com.ahmadabuhasan.pointofsales.expense;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmadabuhasan.pointofsales.Constant;
import com.ahmadabuhasan.pointofsales.R;
import com.ahmadabuhasan.pointofsales.database.DatabaseAccess;
import com.ahmadabuhasan.pointofsales.databinding.ExpenseItemBinding;

import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;

/*
 * Created by Ahmad Abu Hasan (C) 2022
 */

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.MyViewHolder> {

    private final Context context;
    private final List<HashMap<String, String>> expenseData;

    public ExpenseAdapter(Context context1, List<HashMap<String, String>> expenseData1) {
        this.context = context1;
        this.expenseData = expenseData1;
    }

    @NonNull
    @Override
    public ExpenseAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ExpenseItemBinding binding = ExpenseItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseAdapter.MyViewHolder holder, int position) {

        final DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this.context);
        databaseAccess.open();
        String currency = databaseAccess.getCurrency();

        final String expense_id = this.expenseData.get(position).get(Constant.EXPENSE_ID);
        String expense_name = this.expenseData.get(position).get(Constant.EXPENSE_NAME);
        String expense_amount = this.expenseData.get(position).get(Constant.EXPENSE_AMOUNT);
        String expense_date = this.expenseData.get(position).get(Constant.EXPENSE_DATE);
        String expense_time = this.expenseData.get(position).get(Constant.EXPENSE_TIME);
        String expense_note = this.expenseData.get(position).get(Constant.EXPENSE_NOTE);

        holder.binding.tvExpenseName.setText(expense_name);
        holder.binding.tvExpenseAmount.setText(String.format("%s%s", currency, expense_amount));
        holder.binding.tvExpenseDateTime.setText(String.format("%s %s", expense_date, expense_time));
        holder.binding.tvExpenseNote.setText(String.format("%s%s", this.context.getString(R.string.note), expense_note));

        holder.binding.ivExpenseDelete.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
            builder.setMessage(R.string.want_to_delete_expense)
                    .setCancelable(false)
                    .setPositiveButton(R.string.yes, (dialogInterface, i) -> {
                        databaseAccess.open();
                        if (databaseAccess.deleteExpense(expense_id)) {
                            Toasty.error(this.context, R.string.expense_deleted, Toasty.LENGTH_SHORT).show();
                            this.expenseData.remove(holder.getBindingAdapterPosition());
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
        return this.expenseData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final ExpenseItemBinding binding;

        public MyViewHolder(@NonNull ExpenseItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Intent i = new Intent(ExpenseAdapter.this.context, EditExpenseActivity.class);
            i.putExtra(Constant.EXPENSE_ID, ExpenseAdapter.this.expenseData.get(getAbsoluteAdapterPosition()).get(Constant.EXPENSE_ID));
            i.putExtra(Constant.EXPENSE_NAME, ExpenseAdapter.this.expenseData.get(getAbsoluteAdapterPosition()).get(Constant.EXPENSE_NAME));
            i.putExtra(Constant.EXPENSE_AMOUNT, ExpenseAdapter.this.expenseData.get(getAbsoluteAdapterPosition()).get(Constant.EXPENSE_AMOUNT));
            i.putExtra(Constant.EXPENSE_DATE, ExpenseAdapter.this.expenseData.get(getAbsoluteAdapterPosition()).get(Constant.EXPENSE_DATE));
            i.putExtra(Constant.EXPENSE_TIME, ExpenseAdapter.this.expenseData.get(getAbsoluteAdapterPosition()).get(Constant.EXPENSE_TIME));
            i.putExtra(Constant.EXPENSE_NOTE, ExpenseAdapter.this.expenseData.get(getAbsoluteAdapterPosition()).get(Constant.EXPENSE_NOTE));
            context.startActivity(i);
        }
    }
}