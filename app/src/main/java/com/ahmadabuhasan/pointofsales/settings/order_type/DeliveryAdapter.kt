package com.ahmadabuhasan.pointofsales.settings.order_type

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
import com.ahmadabuhasan.pointofsales.databinding.DeliveryItemBinding
import es.dmoral.toasty.Toasty

/*
 * Created by Ahmad Abu Hasan (C) 2022
 */

class DeliveryAdapter(
    private val context: Context,
    private val deliveryData: MutableList<HashMap<String, String>>
) : RecyclerView.Adapter<DeliveryAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = DeliveryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val delivery_id = deliveryData[position][Constant.ORDER_TYPE_ID]

        holder.binding.tvDeliveryName.text = deliveryData[position][Constant.ORDER_TYPE_NAME]
        holder.binding.ivDelete.setOnClickListener {
            AlertDialog.Builder(context)
                .setMessage(R.string.want_to_delete)
                .setCancelable(false)
                .setPositiveButton(R.string.yes) { dialogInterface, _ ->
                    val databaseAccess = DatabaseAccess.getInstance(context)
                    databaseAccess.open()
                    if (databaseAccess.deleteOrderType(delivery_id)) {
                        Toasty.success(context, R.string.delivery_deleted, Toasty.LENGTH_SHORT).show()
                        deliveryData.removeAt(holder.adapterPosition)
                        notifyItemRemoved(holder.adapterPosition)
                    } else {
                        Toasty.error(context, R.string.failed, Toasty.LENGTH_SHORT).show()
                    }
                    dialogInterface.cancel()
                }.setNegativeButton(R.string.no) { dialogInterface, _ -> dialogInterface.cancel() }.show()
        }
    }

    override fun getItemCount(): Int = deliveryData.size

    inner class MyViewHolder(val binding: DeliveryItemBinding) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        init { itemView.setOnClickListener(this) }

        override fun onClick(view: View) {
            val i = Intent(context, EditDeliveryActivity::class.java)
            i.putExtra(Constant.ORDER_TYPE_ID, deliveryData[adapterPosition][Constant.ORDER_TYPE_ID])
            i.putExtra(Constant.ORDER_TYPE_NAME, deliveryData[adapterPosition][Constant.ORDER_TYPE_NAME])
            context.startActivity(i)
        }
    }
}
