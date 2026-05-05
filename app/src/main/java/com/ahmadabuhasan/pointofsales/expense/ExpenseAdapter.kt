package com.ahmadabuhasan.pointofsales.expense

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.ahmadabuhasan.pointofsales.Constant
import com.ahmadabuhasan.pointofsales.R
import com.ahmadabuhasan.pointofsales.database.DatabaseAccess
import com.ahmadabuhasan.pointofsales.databinding.ExpenseItemBinding
import es.dmoral.toasty.Toasty

/*
 * Created by Ahmad Abu Hasan (C) 2022
 */

class ExpenseAdapter(
    private val context: Context,
    private val expenseData: MutableList<HashMap<String, String>>
) : RecyclerView.Adapter<ExpenseAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ExpenseItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val databaseAccess = DatabaseAccess.getInstance(context)
        databaseAccess.open()
        val currency = databaseAccess.currency

        val expenseId = expenseData[position][Constant.EXPENSE_ID]
        val expenseName = expenseData[position][Constant.EXPENSE_NAME]
        val expenseAmount = expenseData[position][Constant.EXPENSE_AMOUNT]
        val expenseDate = expenseData[position][Constant.EXPENSE_DATE]
        val expenseTime = expenseData[position][Constant.EXPENSE_TIME]
        val expenseNote = expenseData[position][Constant.EXPENSE_NOTE]

        holder.binding.tvExpenseName.text = expenseName
        holder.binding.tvExpenseAmount.text = "$currency$expenseAmount"
        holder.binding.tvExpenseDateTime.text = "$expenseDate $expenseTime"
        holder.binding.tvExpenseNote.text = "${context.getString(R.string.note)}$expenseNote"

        holder.binding.ivExpenseDelete.setOnClickListener {
            AlertDialog.Builder(context)
                .setMessage(R.string.want_to_delete_expense)
                .setCancelable(false)
                .setPositiveButton(R.string.yes) { dialog, _ ->
                    databaseAccess.open()
                    if (databaseAccess.deleteExpense(expenseId)) {
                        Toasty.error(context, R.string.expense_deleted, Toasty.LENGTH_SHORT).show()
                        val adapterPosition = holder.bindingAdapterPosition
                        expenseData.removeAt(adapterPosition)
                        notifyItemRemoved(adapterPosition)
                    } else {
                        Toast.makeText(context, R.string.failed, Toast.LENGTH_SHORT).show()
                    }
                    dialog.cancel()
                }
                .setNegativeButton(R.string.no) { dialog, _ -> dialog.cancel() }
                .show()
        }
    }

    override fun getItemCount(): Int = expenseData.size

    inner class MyViewHolder(val binding: ExpenseItemBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            val pos = absoluteAdapterPosition
            val intent = Intent(context, EditExpenseActivity::class.java).apply {
                putExtra(Constant.EXPENSE_ID, expenseData[pos][Constant.EXPENSE_ID])
                putExtra(Constant.EXPENSE_NAME, expenseData[pos][Constant.EXPENSE_NAME])
                putExtra(Constant.EXPENSE_AMOUNT, expenseData[pos][Constant.EXPENSE_AMOUNT])
                putExtra(Constant.EXPENSE_DATE, expenseData[pos][Constant.EXPENSE_DATE])
                putExtra(Constant.EXPENSE_TIME, expenseData[pos][Constant.EXPENSE_TIME])
                putExtra(Constant.EXPENSE_NOTE, expenseData[pos][Constant.EXPENSE_NOTE])
            }
            context.startActivity(intent)
        }
    }
}