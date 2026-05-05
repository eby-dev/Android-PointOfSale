package com.ahmadabuhasan.pointofsales.customers

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.ahmadabuhasan.pointofsales.Constant
import com.ahmadabuhasan.pointofsales.R
import com.ahmadabuhasan.pointofsales.database.DatabaseAccess
import com.ahmadabuhasan.pointofsales.databinding.CustomerItemBinding
import es.dmoral.toasty.Toasty

/*
 * Created by Ahmad Abu Hasan (C) 2022
 */

class CustomerAdapter(
    private val context: Context,
    private val customerData: MutableList<HashMap<String, String>>
) : RecyclerView.Adapter<CustomerAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = CustomerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val customerId = customerData[position][Constant.CUSTOMER_ID]
        val customerCell = customerData[position][Constant.CUSTOMER_CELL]

        holder.binding.tvCustomerName.text = customerData[position][Constant.CUSTOMER_NAME]
        holder.binding.tvCustomerCell.text = customerCell
        holder.binding.tvCustomerEmail.text = customerData[position][Constant.CUSTOMER_EMAIL]
        holder.binding.tvCustomerAddress.text = customerData[position][Constant.CUSTOMER_ADDRESS]

        holder.binding.ivCustomerCall.setOnClickListener {
            val callIntent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$customerCell")
            }
            context.startActivity(callIntent)
        }

        holder.binding.ivCustomerDelete.setOnClickListener {
            AlertDialog.Builder(context)
                .setMessage(R.string.want_to_delete_customer)
                .setCancelable(false)
                .setPositiveButton(R.string.yes) { dialog, _ ->
                    val databaseAccess = DatabaseAccess.getInstance(context)
                    databaseAccess.open()
                    if (databaseAccess.deleteCustomer(customerId)) {
                        Toasty.error(context, R.string.customer_deleted, Toasty.LENGTH_SHORT).show()
                        val adapterPosition = holder.bindingAdapterPosition
                        customerData.removeAt(adapterPosition)
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

    override fun getItemCount(): Int = customerData.size

    inner class MyViewHolder(val binding: CustomerItemBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            val pos = absoluteAdapterPosition
            val intent = Intent(context, EditCustomersActivity::class.java).apply {
                putExtra(Constant.CUSTOMER_ID, customerData[pos][Constant.CUSTOMER_ID])
                putExtra(Constant.CUSTOMER_NAME, customerData[pos][Constant.CUSTOMER_NAME])
                putExtra(Constant.CUSTOMER_CELL, customerData[pos][Constant.CUSTOMER_CELL])
                putExtra(Constant.CUSTOMER_EMAIL, customerData[pos][Constant.CUSTOMER_EMAIL])
                putExtra(Constant.CUSTOMER_ADDRESS, customerData[pos][Constant.CUSTOMER_ADDRESS])
            }
            context.startActivity(intent)
        }
    }
}