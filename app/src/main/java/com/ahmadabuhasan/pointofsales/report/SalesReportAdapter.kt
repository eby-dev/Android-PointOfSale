package com.ahmadabuhasan.pointofsales.report

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ahmadabuhasan.pointofsales.Constant
import com.ahmadabuhasan.pointofsales.R
import com.ahmadabuhasan.pointofsales.database.DatabaseAccess
import com.ahmadabuhasan.pointofsales.databinding.SalesReportItemBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

/*
 * Created by Ahmad Abu Hasan (C) 2022
 */

class SalesReportAdapter(
    private val context: Context,
    private val orderData: List<HashMap<String, String>>
) : RecyclerView.Adapter<SalesReportAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = SalesReportItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val databaseAccess = DatabaseAccess.getInstance(context)

        databaseAccess.open()
        val currency = databaseAccess.currency
        holder.binding.tvProductName.text = orderData[position][Constant.PRODUCT_NAME]
        holder.binding.tvDate.text = String.format("%s%s", context.getString(R.string.date), orderData[position][Constant.PRODUCT_ORDER_DATE])
        holder.binding.tvQty.text = String.format("%s%s", context.getString(R.string.quantity), orderData[position][Constant.PRODUCT_QTY])
        holder.binding.tvWeight.text = String.format("%s%s", context.getString(R.string.weight), orderData[position][Constant.PRODUCT_WEIGHT])

        val unitPrice = orderData[position][Constant.PRODUCT_PRICE]
        val qty = orderData[position][Constant.PRODUCT_QTY]
        val price = unitPrice.orEmpty().toDouble()
        val quantity = qty.orEmpty().toInt()
        val cost = quantity * price
        holder.binding.tvTotalCost.text = String.format("%s%s x %s = %s%s", currency, unitPrice, qty, currency, cost)

        val base64Image = orderData[position][Constant.PRODUCT_IMAGE] ?: return
        if (base64Image.isEmpty() || base64Image.length < 6) {
            Glide.with(holder.itemView.context)
                .load(base64Image)
                .apply(RequestOptions.placeholderOf(R.drawable.ic_loading).error(R.drawable.expense))
                .into(holder.binding.ivSalesReport)
            return
        }
        val bytes = Base64.decode(base64Image, 0)
        holder.binding.ivSalesReport.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.size))
    }

    override fun getItemCount(): Int = orderData.size

    class MyViewHolder(val binding: SalesReportItemBinding) : RecyclerView.ViewHolder(binding.root)
}
