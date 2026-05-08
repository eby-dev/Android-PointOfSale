package com.ahmadabuhasan.pointofsales.product

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.ahmadabuhasan.pointofsales.Constant
import com.ahmadabuhasan.pointofsales.R
import com.ahmadabuhasan.pointofsales.database.DatabaseAccess
import com.ahmadabuhasan.pointofsales.databinding.ProductItemBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import es.dmoral.toasty.Toasty

/*
 * Created by Ahmad Abu Hasan (C) 2022
 */

class ProductAdapter(
    private val context: Context,
    private val productData: MutableList<HashMap<String, String>>
) : RecyclerView.Adapter<ProductAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ProductItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val databaseAccess = DatabaseAccess.getInstance(context)

        val productId = productData[position][Constant.PRODUCT_ID]
        val base64Image = productData[position][Constant.PRODUCT_IMAGE]
        databaseAccess.open()
        val currency = databaseAccess.currency
        databaseAccess.open()
        val supplierName = databaseAccess.getSupplierName(productData[position][Constant.PRODUCT_SUPPLIER])

        holder.binding.tvProductName.text = productData[position][Constant.PRODUCT_NAME]
        holder.binding.tvProductSupplier.text = context.getString(R.string.supplier) + supplierName
        holder.binding.tvProductBuyPrice.text = context.getString(R.string.buy_price) + currency + productData[position][Constant.PRODUCT_BUY_PRICE]
        holder.binding.tvProductSellPrice.text = context.getString(R.string.sell_price) + currency + productData[position][Constant.PRODUCT_SELL_PRICE]

        if (base64Image != null) {
            if (base64Image.length < 6) {
                Log.d("64base", base64Image)
                Glide.with(holder.itemView.context)
                    .load(base64Image)
                    .apply(RequestOptions.placeholderOf(R.drawable.ic_loading).error(R.drawable.image_placeholder))
                    .into(holder.binding.ivProductImage)
            } else {
                val bytes = Base64.decode(base64Image, Base64.DEFAULT)
                holder.binding.ivProductImage.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.size))
            }
        }

        holder.binding.ivDelete.setOnClickListener {
            AlertDialog.Builder(context)
                .setMessage(R.string.want_to_delete_product)
                .setCancelable(false)
                .setPositiveButton(R.string.yes) { dialogInterface, _ ->
                    databaseAccess.open()
                    if (databaseAccess.deleteProduct(productId)) {
                        Toasty.error(context, R.string.product_deleted, Toasty.LENGTH_SHORT).show()
                        productData.removeAt(holder.adapterPosition)
                        notifyItemRemoved(holder.adapterPosition)
                    } else {
                        Toast.makeText(context, R.string.failed, Toast.LENGTH_SHORT).show()
                    }
                    dialogInterface.cancel()
                }.setNegativeButton(R.string.no) { dialogInterface, _ ->
                    dialogInterface.cancel()
                }.show()
        }
    }

    override fun getItemCount(): Int = productData.size

    inner class MyViewHolder(val binding: ProductItemBinding) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            val i = Intent(this@ProductAdapter.context, EditProductActivity::class.java)
            i.putExtra(Constant.PRODUCT_ID, productData[adapterPosition][Constant.PRODUCT_ID].orEmpty())
            context.startActivity(i)
        }
    }
}
