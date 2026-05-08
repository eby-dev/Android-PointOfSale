package com.ahmadabuhasan.pointofsales.orders

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.ahmadabuhasan.pointofsales.Constant
import com.ahmadabuhasan.pointofsales.R
import com.ahmadabuhasan.pointofsales.database.DatabaseAccess
import com.ahmadabuhasan.pointofsales.databinding.OrderItemBinding
import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder
import es.dmoral.toasty.Toasty

/*
 * Created by Ahmad Abu Hasan (C) 2022
 */

class OrderAdapter(
    private val context: Context,
    private val orderData: MutableList<HashMap<String, String>>
) : RecyclerView.Adapter<OrderAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = OrderItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val databaseAccess = DatabaseAccess.getInstance(context)

        val customerName = orderData[position][Constant.CUSTOMER_NAME]
        val invoiceId = orderData[position][Constant.INVOICE_ID]
        val paymentMethod = orderData[position][Constant.ORDER_PAYMENT_METHOD]
        val orderType = orderData[position][Constant.ORDER_TYPE]
        val orderDate = orderData[position][Constant.ORDER_DATE]
        val orderTime = orderData[position][Constant.ORDER_TIME]
        val orderStatus = orderData[position][Constant.ORDER_STATUS]

        holder.binding.tvCustomerName.text = customerName
        holder.binding.tvOrderId.text = String.format("%s%s", context.getString(R.string.order_id), invoiceId)
        holder.binding.tvPaymentMethod.text = String.format("%s%s", context.getString(R.string.payment_method), paymentMethod)
        holder.binding.tvOrderType.text = String.format("%s:%s", context.getString(R.string.order_type), orderType)
        holder.binding.tvDate.text = String.format("%s %s", orderTime, orderDate)
        holder.binding.tvOrderStatus.text = orderStatus

        if (orderStatus == Constant.COMPLETED) {
            holder.binding.tvOrderStatus.setBackgroundColor(Color.parseColor("#43A047"))
            holder.binding.tvOrderStatus.setTextColor(Color.WHITE)
            holder.binding.ivEditStatus.visibility = View.GONE
        } else if (orderStatus == Constant.CANCEL) {
            holder.binding.tvOrderStatus.setBackgroundColor(Color.parseColor("#E53935"))
            holder.binding.tvOrderStatus.setTextColor(Color.WHITE)
            holder.binding.ivEditStatus.visibility = View.GONE
        }

        holder.binding.ivEditStatus.setOnClickListener {
            val dialogBuilder = NiftyDialogBuilder.getInstance(context)
            dialogBuilder.withTitle(context.getString(R.string.change_order_status))
                .withMessage(context.getString(R.string.please_change_order_status_to_complete_or_cancel))
                .withEffect(Effectstype.Slidetop)
                .withDialogColor("#01BAEF")
                .withButton1Text(context.getString(R.string.order_completed))
                .withButton2Text(context.getString(R.string.cancel_order))
                .setButton1Click { _ ->
                    databaseAccess.open()
                    if (databaseAccess.updateOrder(invoiceId, Constant.COMPLETED)) {
                        Toasty.success(context, R.string.order_updated, Toasty.LENGTH_SHORT).show()
                        holder.binding.tvOrderStatus.setText(Constant.COMPLETED)
                        holder.binding.tvOrderStatus.setTextColor(Color.WHITE)
                        holder.binding.tvOrderStatus.setBackgroundColor(Color.parseColor("#43A047"))
                        holder.binding.ivEditStatus.visibility = View.GONE
                    } else {
                        Toast.makeText(context, R.string.failed, Toast.LENGTH_SHORT).show()
                    }
                    dialogBuilder.dismiss()
                }.setButton2Click { _ ->
                    databaseAccess.open()
                    if (databaseAccess.updateOrder(invoiceId, Constant.CANCEL)) {
                        Toasty.success(context, R.string.order_updated, Toasty.LENGTH_SHORT).show()
                        holder.binding.tvOrderStatus.setText(Constant.CANCEL)
                        holder.binding.tvOrderStatus.setTextColor(Color.WHITE)
                        holder.binding.tvOrderStatus.setBackgroundColor(Color.parseColor("#E53935"))
                        holder.binding.ivEditStatus.visibility = View.GONE
                    } else {
                        Toast.makeText(context, R.string.failed, Toast.LENGTH_SHORT).show()
                    }
                    dialogBuilder.dismiss()
                }.show()
        }

        holder.itemView.setOnLongClickListener {
            AlertDialog.Builder(context)
                .setMessage("Want to Delete Order ?")
                .setCancelable(false)
                .setPositiveButton(R.string.yes) { dialog, _ ->
                    databaseAccess.open()
                    if (databaseAccess.deleteOrder(invoiceId)) {
                        Toasty.error(context, "Order Deleted", Toast.LENGTH_SHORT).show()
                        orderData.removeAt(holder.bindingAdapterPosition)
                        notifyItemRemoved(holder.bindingAdapterPosition)
                    } else {
                        Toast.makeText(context, R.string.failed, Toast.LENGTH_SHORT).show()
                    }
                    dialog.cancel()
                }
                .setNegativeButton(R.string.no) { dialog, _ -> dialog.cancel() }.show()
            false
        }
    }

    override fun getItemCount(): Int = orderData.size

    inner class MyViewHolder(val binding: OrderItemBinding) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        init { itemView.setOnClickListener(this) }

        override fun onClick(view: View) {
            val i = Intent(context, OrderDetailsActivity::class.java)
            i.putExtra(Constant.ORDER_ID, orderData[absoluteAdapterPosition][Constant.INVOICE_ID])
            i.putExtra(Constant.CUSTOMER_NAME, orderData[absoluteAdapterPosition][Constant.CUSTOMER_NAME])
            i.putExtra(Constant.ORDER_DATE, orderData[absoluteAdapterPosition][Constant.ORDER_DATE])
            i.putExtra(Constant.ORDER_TIME, orderData[absoluteAdapterPosition][Constant.ORDER_TIME])
            i.putExtra(Constant.TAX, orderData[absoluteAdapterPosition][Constant.TAX])
            i.putExtra(Constant.DISCOUNT, orderData[absoluteAdapterPosition][Constant.DISCOUNT])
            context.startActivity(i)
        }
    }
}
