package com.ahmadabuhasan.pointofsales.pos

import android.content.Context
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ahmadabuhasan.pointofsales.Constant
import com.ahmadabuhasan.pointofsales.R
import com.ahmadabuhasan.pointofsales.database.DatabaseAccess
import com.ahmadabuhasan.pointofsales.databinding.CartProductItemsBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import es.dmoral.toasty.Toasty
import java.text.MessageFormat
import java.text.NumberFormat
import java.util.Locale

/*
 * Created by Ahmad Abu Hasan (C) 2022
 */

class CartAdapter(
    private val context: Context,
    private val cartProduct: MutableList<HashMap<String, String>>,
    private val ivNoCart: ImageView,
    private val tvNoCart: TextView,
    private val tvTotalPrice: TextView,
    private val btnSubmitOrder: Button
) : RecyclerView.Adapter<CartAdapter.MyViewHolder>() {

    companion object {
        var totalPrice: Double? = null
    }

    private val sound: MediaPlayer = MediaPlayer.create(context, R.raw.delete_sound)
    private val locale = Locale("in", "ID")
    private val formatIDR = NumberFormat.getInstance(locale)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = CartProductItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val databaseAccess = DatabaseAccess.getInstance(context)

        val cartId = cartProduct[position][Constant.CART_ID]
        val productId = cartProduct[position][Constant.PRODUCT_ID]
        databaseAccess.open()
        val productName = databaseAccess.getProductName(productId)
        val weight = cartProduct[position][Constant.PRODUCT_WEIGHT]
        val weightUnitId = cartProduct[position][Constant.PRODUCT_WEIGHT_UNIT]
        val price = cartProduct[position][Constant.PRODUCT_PRICE]
        val qty = cartProduct[position][Constant.PRODUCT_QTY]
        val stock = cartProduct[position][Constant.STOCK]

        val getStock = stock.orEmpty().toInt()

        databaseAccess.open()
        val base64Image = databaseAccess.getProductImage(productId)

        databaseAccess.open()
        val weightUnitName = databaseAccess.getWeightUnitName(weightUnitId)

        databaseAccess.open()
        val currency = databaseAccess.currency

        databaseAccess.open()
        totalPrice = databaseAccess.totalPrice
        tvTotalPrice.text = String.format("%s %s%s", context.getString(R.string.total_price), currency, formatIDR.format(totalPrice))

        if (base64Image != null) {
            if (base64Image.isEmpty() || base64Image.length < 6) {
                Glide.with(holder.itemView.context)
                    .load(base64Image)
                    .apply(RequestOptions.placeholderOf(R.drawable.ic_loading).error(R.drawable.image_placeholder))
                    .into(holder.binding.ivCartProduct)
            } else {
                val bytes = Base64.decode(base64Image, Base64.DEFAULT)
                holder.binding.ivCartProduct.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.size))
            }
        }

        val getPrice = price.orEmpty().toDouble() * qty.orEmpty().toInt()

        holder.binding.tvItemName.text = productName
        holder.binding.tvWeight.text = String.format("%s %s", weight, weightUnitName)
        holder.binding.tvQtyNumber.text = qty
        holder.binding.tvPrice.text = String.format("%s%s", currency, formatIDR.format(getPrice))

        holder.binding.tvMinus.setOnClickListener {
            val qtyMinus = holder.binding.tvQtyNumber.text.toString()
            var getQty = qtyMinus.toInt()
            if (getQty >= 2) {
                getQty--
                val cost = (price?.toDouble() ?: 0.0) * getQty

                holder.binding.tvPrice.text = String.format("%s%s", currency, formatIDR.format(cost))
                holder.binding.tvQtyNumber.text = MessageFormat.format("{0}", getQty)

                databaseAccess.open()
                databaseAccess.updateProductQty(cartId, "" + getQty)
                totalPrice = (totalPrice ?: 0.0) - (price?.toDouble() ?: 0.0)
                tvTotalPrice.text = String.format("%s %s%s", context.getString(R.string.total_price), currency, formatIDR.format(totalPrice))
            }
        }

        holder.binding.tvPlus.setOnClickListener {
            val qtyPlus = holder.binding.tvQtyNumber.text.toString()
            var getQty = qtyPlus.toInt()
            if (getQty >= getStock) {
                Toasty.error(context, R.string.available_stock, Toasty.LENGTH_SHORT).show()
            } else {
                getQty++
                val cost = (price?.toDouble() ?: 0.0) * getQty

                holder.binding.tvPrice.text = String.format("%s%s", currency, formatIDR.format(cost))
                holder.binding.tvQtyNumber.text = MessageFormat.format("{0}", getQty)

                databaseAccess.open()
                databaseAccess.updateProductQty(cartId, "" + getQty)
                totalPrice = (totalPrice ?: 0.0) + (price?.toDouble() ?: 0.0)
                tvTotalPrice.text = String.format("%s %s%s", context.getString(R.string.total_price), currency, formatIDR.format(totalPrice))
            }
        }

        holder.binding.ivDelete.setOnClickListener {
            databaseAccess.open()
            if (databaseAccess.deleteProductFromCart(cartId)) {
                Toasty.success(context, R.string.product_removed_from_cart, Toasty.LENGTH_SHORT).show()
                sound.start()
                cartProduct.removeAt(holder.bindingAdapterPosition)
                notifyItemRemoved(holder.bindingAdapterPosition)

                databaseAccess.open()
                totalPrice = databaseAccess.totalPrice
                tvTotalPrice.text = String.format("%s %s%s", context.getString(R.string.total_price), currency, formatIDR.format(totalPrice))
            } else {
                Toasty.error(context, R.string.failed, Toasty.LENGTH_SHORT).show()
            }

            databaseAccess.open()
            val itemCount = databaseAccess.cartItemCount
            Log.d("itemCount", "" + itemCount)
            if (itemCount <= 0) {
                tvTotalPrice.visibility = View.GONE
                btnSubmitOrder.visibility = View.GONE
                ivNoCart.visibility = View.VISIBLE
                tvNoCart.visibility = View.VISIBLE
            }
        }
    }

    override fun getItemCount(): Int = cartProduct.size

    class MyViewHolder(val binding: CartProductItemsBinding) : RecyclerView.ViewHolder(binding.root)
}
