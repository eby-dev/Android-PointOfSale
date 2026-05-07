package com.ahmadabuhasan.pointofsales.pos

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.MediaPlayer
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ahmadabuhasan.pointofsales.Constant
import com.ahmadabuhasan.pointofsales.R
import com.ahmadabuhasan.pointofsales.database.DatabaseAccess
import com.ahmadabuhasan.pointofsales.databinding.PosProductItemBinding
import com.ahmadabuhasan.pointofsales.product.EditProductActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import es.dmoral.toasty.Toasty
import java.text.NumberFormat
import java.util.Locale

/*
 * Created by Ahmad Abu Hasan (C) 2022
 */

class PosProductAdapter(
    private val context: Context,
    private val productData: List<HashMap<String, String>>
) : RecyclerView.Adapter<PosProductAdapter.MyViewHolder>() {

    private val sound: MediaPlayer = MediaPlayer.create(context, R.raw.delete_sound)
    private val locale = Locale("in", "ID")
    private val formatIDR = NumberFormat.getInstance(locale)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = PosProductItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val databaseAccess = DatabaseAccess.getInstance(context)

        val product_id = productData[position][Constant.PRODUCT_ID]
        val product_name = productData[position][Constant.PRODUCT_NAME]
        val product_stock = productData[position][Constant.PRODUCT_STOCK]
        val product_weight = productData[position][Constant.PRODUCT_WEIGHT]
        val product_weightUnitID = productData[position][Constant.PRODUCT_WEIGHT_UNIT_ID]
        val product_price = productData[position][Constant.PRODUCT_SELL_PRICE]
        val base64Image = productData[position][Constant.PRODUCT_IMAGE]

        holder.binding.tvProductName.text = product_name

        val getStock = product_stock!!.toInt()
        holder.binding.tvStock.text = String.format("%s: %s", context.getString(R.string.stock), product_stock)
        if (getStock <= 5) {
            holder.binding.tvStock.setTextColor(Color.RED)
        }

        databaseAccess.open()
        val weightUnit_name = databaseAccess.getWeightUnitName(product_weightUnitID)
        holder.binding.tvWeight.text = String.format("%s %s", product_weight, weightUnit_name)

        databaseAccess.open()
        val currency = databaseAccess.currency
        val convert = product_price!!.toDouble()
        holder.binding.tvPrice.text = String.format("%s%s", currency, formatIDR.format(convert))

        if (base64Image != null) {
            if (base64Image.length < 6) {
                Log.d("64base", base64Image)
                Glide.with(holder.itemView.context)
                    .load(base64Image)
                    .fitCenter()
                    .apply(RequestOptions.placeholderOf(R.drawable.ic_loading).error(R.drawable.image_placeholder))
                    .into(holder.binding.ivProduct)
            } else {
                val bytes = Base64.decode(base64Image, Base64.DEFAULT)
                holder.binding.ivProduct.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.size))
            }
        }

        holder.binding.cvProduct.setOnClickListener {
            sound.start()
            val i = Intent(context, EditProductActivity::class.java)
            i.putExtra(Constant.PRODUCT_ID, product_id)
            context.startActivity(i)
        }

        holder.binding.btnAddCart.setOnClickListener {
            if (getStock <= 0) {
                Toasty.warning(context, R.string.stock_is_low_please_update_stock, Toasty.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            Log.d("weightUnitID", product_weightUnitID.orEmpty())

            databaseAccess.open()
            val check = databaseAccess.addToCart(product_id, product_weight, product_weightUnitID, product_price, 1, product_stock)
            if (check == 1) {
                Toasty.success(context, R.string.product_added_to_cart, Toasty.LENGTH_SHORT).show()
                sound.start()
            } else if (check == 2) {
                Toasty.info(context, R.string.product_already_added_to_cart, Toasty.LENGTH_SHORT).show()
            } else {
                Toasty.error(context, R.string.product_added_to_cart_failed_try_again, Toasty.LENGTH_SHORT).show()
            }

            databaseAccess.open()
            val count1 = databaseAccess.cartItemCount
            if (count1 == 0) {
                PosActivity.tvCount.visibility = View.INVISIBLE
            } else {
                PosActivity.tvCount.visibility = View.VISIBLE
                PosActivity.tvCount.text = count1.toString()
            }
        }
    }

    override fun getItemCount(): Int = productData.size

    class MyViewHolder(val binding: PosProductItemBinding) : RecyclerView.ViewHolder(binding.root)
}
