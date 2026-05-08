package com.ahmadabuhasan.pointofsales.settings.payment_method

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ahmadabuhasan.pointofsales.Constant
import com.ahmadabuhasan.pointofsales.R
import com.ahmadabuhasan.pointofsales.database.DatabaseAccess
import com.ahmadabuhasan.pointofsales.databinding.PaymentMethodItemBinding
import es.dmoral.toasty.Toasty

/*
 * Created by Ahmad Abu Hasan (C) 2022
 */

class PaymentMethodAdapter(
    private val context: Context,
    private val paymentMethodData: MutableList<HashMap<String, String>>
) : RecyclerView.Adapter<PaymentMethodAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = PaymentMethodItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val paymentMethodId = paymentMethodData[position][Constant.PAYMENT_METHOD_ID]

        holder.binding.tvPaymentMethodName.text = paymentMethodData[position][Constant.PAYMENT_METHOD_NAME]
        holder.binding.ivDelete.setOnClickListener {
            AlertDialog.Builder(context)
                .setMessage(R.string.want_to_delete)
                .setCancelable(false)
                .setPositiveButton(R.string.yes) { dialogInterface, _ ->
                    val databaseAccess = DatabaseAccess.getInstance(context)
                    databaseAccess.open()
                    if (databaseAccess.deletePaymentMethod(paymentMethodId)) {
                        Toasty.success(context, R.string.payment_method_deleted, Toasty.LENGTH_SHORT).show()
                        paymentMethodData.removeAt(holder.adapterPosition)
                        notifyItemRemoved(holder.adapterPosition)
                    } else {
                        Toasty.error(context, R.string.failed, Toasty.LENGTH_SHORT).show()
                    }
                    dialogInterface.cancel()
                }.setNegativeButton(R.string.no) { dialogInterface, _ -> dialogInterface.cancel() }.show()
        }
    }

    override fun getItemCount(): Int = paymentMethodData.size

    inner class MyViewHolder(val binding: PaymentMethodItemBinding) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        init { itemView.setOnClickListener(this) }

        override fun onClick(view: View) {
            val i = Intent(context, EditPaymentMethodActivity::class.java)
            i.putExtra(Constant.PAYMENT_METHOD_ID, paymentMethodData[adapterPosition][Constant.PAYMENT_METHOD_ID].orEmpty())
            i.putExtra(Constant.PAYMENT_METHOD_NAME, paymentMethodData[adapterPosition][Constant.PAYMENT_METHOD_NAME].orEmpty())
            context.startActivity(i)
        }
    }
}
