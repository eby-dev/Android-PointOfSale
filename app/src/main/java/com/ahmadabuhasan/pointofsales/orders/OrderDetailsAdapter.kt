package com.ahmadabuhasan.pointofsales.orders

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ahmadabuhasan.pointofsales.Constant
import com.ahmadabuhasan.pointofsales.R
import com.ahmadabuhasan.pointofsales.database.DatabaseAccess
import com.ahmadabuhasan.pointofsales.databinding.OrderDetailsItemBinding
import java.text.DecimalFormat

/*
 * Created by Ahmad Abu Hasan (C) 2022
 */

class OrderDetailsAdapter(
    private val context: Context,
    private val orderData: List<HashMap<String, String>>
) : RecyclerView.Adapter<OrderDetailsAdapter.MyViewHolder>() {

    val decimalFormat = DecimalFormat("#0.00")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = OrderDetailsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val databaseAccess = DatabaseAccess.getInstance(context)

        holder.binding.tvProductName.text = orderData[position][Constant.PRODUCT_NAME]
        holder.binding.tvQty.text = String.format("%s%s", context.getString(R.string.quantity), orderData[position][Constant.PRODUCT_QTY])
        holder.binding.tvWeight.text = String.format("%s:%s", context.getString(R.string.weight), orderData[position][Constant.PRODUCT_WEIGHT])

        val base64Image = orderData[position][Constant.PRODUCT_IMAGE]
        val unit_price = orderData[position][Constant.PRODUCT_PRICE]
        val qty = orderData[position][Constant.PRODUCT_QTY]

        val cost = unit_price!!.toDouble() * qty!!.toInt()

        databaseAccess.open()
        val currency = databaseAccess.currency
        holder.binding.tvTotalCost.text = String.format("%s%s x %s = %s%s", currency, unit_price, qty, currency, decimalFormat.format(cost))

        if (base64Image == null) {
            return
        }
        if (base64Image.isEmpty() || base64Image.length < 6) {
            holder.binding.ivProduct.setImageResource(R.drawable.image_placeholder)
            return
        }
        val bytes = Base64.decode(base64Image, Base64.DEFAULT)
        holder.binding.ivProduct.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.size))
    }

    override fun getItemCount(): Int = orderData.size

    class MyViewHolder(val binding: OrderDetailsItemBinding) : RecyclerView.ViewHolder(binding.root)
}
