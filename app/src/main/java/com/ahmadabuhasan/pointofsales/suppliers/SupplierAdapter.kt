package com.ahmadabuhasan.pointofsales.suppliers

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
import com.ahmadabuhasan.pointofsales.databinding.SupplierItemBinding
import es.dmoral.toasty.Toasty
import androidx.core.net.toUri

/*
 * Created by Ahmad Abu Hasan (C) 2022
 */

class SupplierAdapter(
    private val context: Context,
    private val supplierData: MutableList<HashMap<String, String>>
) : RecyclerView.Adapter<SupplierAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = SupplierItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val supplierId = supplierData[position][Constant.SUPPLIERS_ID]
        val supplierCell = supplierData[position][Constant.SUPPLIERS_CELL]

        holder.binding.tvSupplierName.text = supplierData[position][Constant.SUPPLIERS_NAME]
        holder.binding.tvSupplierContactPerson.text = supplierData[position][Constant.SUPPLIERS_CONTACT_PERSON]
        holder.binding.tvSupplierCell.text = supplierCell
        holder.binding.tvSupplierEmail.text = supplierData[position][Constant.SUPPLIERS_EMAIL]
        holder.binding.tvSupplierAddress.text = supplierData[position][Constant.SUPPLIERS_ADDRESS]

        holder.binding.ivSupplierCall.setOnClickListener {
            val callIntent = Intent(Intent.ACTION_DIAL).apply {
                data = "tel:$supplierCell".toUri()
            }
            context.startActivity(callIntent)
        }

        holder.binding.ivSupplierDelete.setOnClickListener {
            AlertDialog.Builder(context)
                .setMessage(R.string.want_to_delete_supplier)
                .setCancelable(false)
                .setPositiveButton(R.string.yes) { dialog, _ ->
                    val databaseAccess = DatabaseAccess.getInstance(context)
                    databaseAccess.open()
                    if (databaseAccess.deleteSupplier(supplierId)) {
                        Toasty.success(context, R.string.supplier_deleted, Toasty.LENGTH_SHORT).show()
                        val adapterPosition = holder.bindingAdapterPosition
                        supplierData.removeAt(adapterPosition)
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

    override fun getItemCount(): Int = supplierData.size

    inner class MyViewHolder(val binding: SupplierItemBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            val pos = absoluteAdapterPosition
            val intent = Intent(context, EditSuppliersActivity::class.java).apply {
                putExtra(Constant.SUPPLIERS_ID, supplierData[pos][Constant.SUPPLIERS_ID])
                putExtra(Constant.SUPPLIERS_NAME, supplierData[pos][Constant.SUPPLIERS_NAME])
                putExtra(Constant.SUPPLIERS_CONTACT_PERSON, supplierData[pos][Constant.SUPPLIERS_CONTACT_PERSON])
                putExtra(Constant.SUPPLIERS_CELL, supplierData[pos][Constant.SUPPLIERS_CELL])
                putExtra(Constant.SUPPLIERS_EMAIL, supplierData[pos][Constant.SUPPLIERS_EMAIL])
                putExtra(Constant.SUPPLIERS_ADDRESS, supplierData[pos][Constant.SUPPLIERS_ADDRESS])
            }
            context.startActivity(intent)
        }
    }
}