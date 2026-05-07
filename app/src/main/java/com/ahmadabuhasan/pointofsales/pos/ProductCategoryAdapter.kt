package com.ahmadabuhasan.pointofsales.pos

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ahmadabuhasan.pointofsales.Constant
import com.ahmadabuhasan.pointofsales.R
import com.ahmadabuhasan.pointofsales.database.DatabaseAccess
import com.ahmadabuhasan.pointofsales.databinding.ProductCategoryItemBinding
import com.ahmadabuhasan.pointofsales.settings.categories.EditCategoryActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

/*
 * Created by Ahmad Abu Hasan (C) 2022
 */

class ProductCategoryAdapter(
    private val context: Context,
    private val categoryData: List<HashMap<String, String>>,
    private val recyclerView: RecyclerView,
    private val ivNoData: ImageView,
    private val tvNoData: TextView
) : RecyclerView.Adapter<ProductCategoryAdapter.MyViewHolder>() {

    private val sound: MediaPlayer = MediaPlayer.create(context, R.raw.delete_sound)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ProductCategoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val databaseAccess = DatabaseAccess.getInstance(context)

        val category_id = categoryData[position][Constant.CATEGORY_ID]
        val category_name = categoryData[position][Constant.CATEGORY_NAME]

        holder.binding.tvCategoryName.text = category_name
        holder.binding.cvCategory.setOnClickListener {
            sound.start()

            databaseAccess.open()
            val fromCategoryList = databaseAccess.getTabProducts(category_id)
            if (fromCategoryList.size <= 0) {
                recyclerView.visibility = View.GONE
                tvNoData.visibility = View.VISIBLE
                ivNoData.visibility = View.VISIBLE
                Glide.with(holder.itemView.context)
                    .load(R.drawable.not_found)
                    .apply(RequestOptions.placeholderOf(R.drawable.ic_loading).error(R.drawable.image_placeholder))
                    .into(ivNoData)
            } else {
                tvNoData.visibility = View.GONE
                ivNoData.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE

                val adapter = PosProductAdapter(context, fromCategoryList)
                recyclerView.adapter = adapter
            }
        }
    }

    override fun getItemCount(): Int = categoryData.size

    inner class MyViewHolder(val binding: ProductCategoryItemBinding) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        init { itemView.setOnClickListener(this) }

        override fun onClick(view: View) {
            val i = Intent(context, EditCategoryActivity::class.java)
            i.putExtra(Constant.CATEGORY_ID, categoryData[absoluteAdapterPosition][Constant.CATEGORY_ID])
            i.putExtra(Constant.CATEGORY_NAME, categoryData[absoluteAdapterPosition][Constant.CATEGORY_NAME])
        }
    }
}
